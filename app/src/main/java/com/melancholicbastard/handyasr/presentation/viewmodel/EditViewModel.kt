package com.melancholicbastard.handyasr.presentation.viewmodel

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melancholicbastard.handyasr.domain.decode.DecodeAudioUseCase
import com.melancholicbastard.handyasr.domain.decode.DecodeResult
import com.melancholicbastard.handyasr.domain.editor.DeleteFromCacheUseCase
import com.melancholicbastard.handyasr.domain.editor.ReplaceFromCacheUseCase
import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.usecases.AddNodeUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.GetNodeByIdUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.UpdateNodeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class EditViewModel(
    entity: String,
    private val isNewRecord: Boolean,
    private val replaceFromCache: ReplaceFromCacheUseCase,
    private val deleteFromCache: DeleteFromCacheUseCase,
    private val decodeAudioUseCase: DecodeAudioUseCase,
    private val addNodeUseCase: AddNodeUseCase,
    private val updateNodeUseCase: UpdateNodeUseCase,
    private val getNodeByIdUseCase: GetNodeByIdUseCase,
    private val onNodeSaved: () -> Unit,
    private val onBackClick: () -> Unit
) : ViewModel() {
    companion object {
        private const val TAG = "EditViewModel"
    }

    private var audioFile: File? = null
    private var shouldCleanupTempFileOnClear: Boolean = true
    private var nodeToUpdate: Node? = null

    private var mediaPlayer: MediaPlayer? = null
    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState: StateFlow<PlayerUiState> = _playerUiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _textUiState = MutableStateFlow<TextUiState>(TextUiState.UndefinedTextState)
    val textUiState: StateFlow<TextUiState> = _textUiState.asStateFlow()

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val cleanupScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        if (isNewRecord) {
            audioFile = File(entity)
            if (audioFile?.exists() == true) {
                preparePlayerFromFile()
            } else {
                _playerUiState.value = _playerUiState.value.copy(
                    isLoading = false,
                    error = "Audio file not found: ${audioFile?.absolutePath}"
                )
            }
        } else {
            val nodeId = entity.toLongOrNull()
            if (nodeId == null) {
                _playerUiState.value = _playerUiState.value.copy(
                    error = "Некорректный id записи: $entity"
                )
            } else {
                viewModelScope.launch {
                    val node = getNodeByIdUseCase(nodeId)
                    if (node == null) {
                        _playerUiState.value = _playerUiState.value.copy(
                            error = "Запись с id=$nodeId не найдена"
                        )
                        return@launch
                    }
                    nodeToUpdate = node

                    _title.value = node.title
                    _text.value = node.text.orEmpty()
                    _textUiState.value = if (node.text.isNullOrBlank()) {
                        TextUiState.UndefinedTextState
                    } else {
                        TextUiState.DefinedTextState
                    }
                    audioFile = File(node.audioFileName)
                    shouldCleanupTempFileOnClear = false
                    if (audioFile?.exists() == true) {
                        preparePlayerFromFile()
                    } else {
                        _playerUiState.value = _playerUiState.value.copy(
                            error = "Аудиофайл не найден: ${audioFile?.absolutePath}"
                        )
                    }
                }
            }
        }
    }

    fun setTitle(value: String) {
        _title.value = value
    }

    private var progressJob: Job? = null

    fun setText(value: String) {
        _text.value = value
    }

    fun requestText() {
        val file = audioFile
        if (file == null || !file.exists()) {
            _playerUiState.value = _playerUiState.value.copy(error = "Аудиофайл не найден")
            return
        }

        _textUiState.value = TextUiState.ProcessTextState
        viewModelScope.launch {
            val result = decodeAudioUseCase(file)

            when (result) {
                is DecodeResult.Success -> {
                    _text.value = result.text
                    _textUiState.value = TextUiState.DefinedTextState
                }

                is DecodeResult.Error -> {
                    Log.e(TAG, "decode request failed: ${result.detail}")
                    _playerUiState.value = _playerUiState.value.copy(error = result.detail)
                    _textUiState.value = TextUiState.UndefinedTextState
                }
            }
        }
    }

    fun saveNode() {
        if (_isSaving.value) return
        val sourceFile = audioFile
        if (sourceFile == null || !sourceFile.exists()) {
            _playerUiState.value = _playerUiState.value.copy(error = "Аудиофайл не найден")
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            try {
                val persistentFile = if (isNewRecord) {
                    val copied = replaceFromCache(sourceFile)
                    deleteFromCache(sourceFile)
                    shouldCleanupTempFileOnClear = false
                    copied
                } else {
                    sourceFile
                }

                if (isNewRecord) {
                    val nodeToSave = Node(
                        title = _title.value.ifBlank { "новая запись" },
                        text = _text.value.takeIf { it.isNotBlank() },
                        createdAt = System.currentTimeMillis(),
                        audioFileName = persistentFile.absolutePath
                    )
                    val newId = addNodeUseCase(nodeToSave)
                    Log.d(TAG, "Created node: ${nodeToSave.copy(id = newId)}")
                } else {
                    if (nodeToUpdate == null) {
                        Log.e(TAG, "Cannot update node: nodeToUpdate is null")
                        _playerUiState.value = _playerUiState.value.copy(error = "Внутренняя ошибка: id записи не известен")
                        return@launch
                    }

                    val nodeToUpdate = nodeToUpdate!!.copy(
                        title = _title.value.ifBlank { "новая запись" },
                        text = _text.value.takeIf { it.isNotBlank() }
                    )

                    updateNodeUseCase(nodeToUpdate)
                    Log.d(TAG, "Updated node: $nodeToUpdate")
                }

                onNodeSaved()
            } catch (t: Throwable) {
                Log.e(TAG, "failed to save node", t)
                _playerUiState.value = _playerUiState.value.copy(
                    error = t.message
                )
            } finally {
                _isSaving.value = false
            }
        }
    }

    private fun preparePlayerFromFile() {
        val file = audioFile ?: return
        releasePlayer()
        _playerUiState.value = _playerUiState.value.copy(
            isLoading = true,
            error = null,
            isPlaying = false,
            positionMs = 0,
            durationMs = 0
        )

        mediaPlayer = MediaPlayer().apply {
            try {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(file.absolutePath)
                setOnPreparedListener { mp ->
                    _playerUiState.value = _playerUiState.value.copy(
                        isLoading = false,
                        durationMs = mp.duration,
                        positionMs = mp.currentPosition,
                        isPlaying = false
                    )
                }
                setOnCompletionListener {
                    _playerUiState.value =
                        _playerUiState.value.copy(isPlaying = false, positionMs = duration)
                    stopProgressUpdates()
                }
                prepareAsync()
            } catch (e: IOException) {
                Log.e(TAG, "prepareFromFile error", e)
                _playerUiState.value =
                    _playerUiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.also { mp ->
            if (mp.isPlaying) pause() else play()
        }
    }

    fun play() {
        mediaPlayer?.also { mp ->
            mp.start()
            _playerUiState.value = _playerUiState.value.copy(isPlaying = true)
            startProgressUpdates()
        }
    }

    fun pause() {
        mediaPlayer?.also { mp ->
            mp.pause()
            _playerUiState.value = _playerUiState.value.copy(isPlaying = false)
            stopProgressUpdates()
        }
    }

    fun seekTo(ms: Int) {
        mediaPlayer?.also { mp ->
            val to = ms.coerceIn(0, mp.duration)
            mp.seekTo(to)
            _playerUiState.value = _playerUiState.value.copy(positionMs = to)
        }
    }

    fun skipForward(byMs: Int = 5000) {
        mediaPlayer?.also { mp ->
            seekTo((mp.currentPosition + byMs).coerceAtMost(mp.duration))
        }
    }

    fun skipBackward(byMs: Int = 5000) {
        mediaPlayer?.also { mp ->
            seekTo((mp.currentPosition - byMs).coerceAtLeast(0))
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                mediaPlayer?.also { mp ->
                    if (!mp.isPlaying) break
                    try {
                        _playerUiState.value =
                            _playerUiState.value.copy(positionMs = mp.currentPosition)
                    } catch (t: Throwable) {
                        Log.w(TAG, "progress update failed", t)
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun releasePlayer() {
        try {
            stopProgressUpdates()
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (t: Throwable) {
            Log.w(TAG, "release failed", t)
        } finally {
            mediaPlayer = null
            _playerUiState.value = PlayerUiState()
        }
    }

    fun onBackButtonPressed() {
        if (shouldCleanupTempFileOnClear && audioFile != null && audioFile!!.exists()) {
            cleanupScope.launch { deleteFromCache(audioFile!!) }
        }
        onBackClick()
    }

    override fun onCleared() {
        if (shouldCleanupTempFileOnClear && audioFile != null && audioFile!!.exists()) {
            cleanupScope.launch { deleteFromCache(audioFile!!) }
        }
        super.onCleared()
        releasePlayer()
    }
}

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val durationMs: Int = 0,
    val positionMs: Int = 0,
    val error: String? = null
)

sealed class TextUiState {
    object UndefinedTextState : TextUiState()
    object ProcessTextState : TextUiState()
    object DefinedTextState : TextUiState()
}
