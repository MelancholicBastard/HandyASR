package com.melancholicbastard.handyasr.presentation.viewmodel

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melancholicbastard.handyasr.domain.editor.DeleteFromCacheUseCase
import com.melancholicbastard.handyasr.domain.editor.ReplaceFromCacheUseCase
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
	private val isNewRecord: Boolean,
	private val entity: String,
	private val replaceFromCache : ReplaceFromCacheUseCase,
	private val deleteFromCache : DeleteFromCacheUseCase
) : ViewModel() {
	companion object {
		private const val TAG = "EditViewModel"
	}

	private var audioFile: File? = null

	private var mediaPlayer: MediaPlayer? = null
	private val _uiState = MutableStateFlow(PlayerUiState())

	val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
	private var progressJob: Job? = null

	private val cleanupScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

	init {
		if (isNewRecord) {
			audioFile = File(entity)
			if (audioFile?.exists() == true) {
				preparePlayerFromFile()
			} else {
				_uiState.value = _uiState.value.copy(isLoading = false, error = "Audio file not found: ${audioFile?.absolutePath}")
			}
		} else {
			//
		}
	}

	private fun preparePlayerFromFile() {
		val file = audioFile ?: return
		releasePlayer()
		_uiState.value = _uiState.value.copy(isLoading = true, error = null, isPlaying = false, positionMs = 0, durationMs = 0)

		mediaPlayer = MediaPlayer().apply {
			try {
				setAudioAttributes(
					AudioAttributes.Builder()
						.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
						.build()
				)
				setDataSource(file.absolutePath)
				setOnPreparedListener { mp ->
					_uiState.value = _uiState.value.copy(
						isLoading = false,
						durationMs = mp.duration,
						positionMs = mp.currentPosition,
						isPlaying = false
					)
				}
				setOnCompletionListener {
					_uiState.value = _uiState.value.copy(isPlaying = false, positionMs = duration)
					stopProgressUpdates()
				}
				prepareAsync()
			} catch (e: IOException) {
				Log.e(TAG, "prepareFromFile error", e)
				_uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
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
			_uiState.value = _uiState.value.copy(isPlaying = true)
			startProgressUpdates()
		}
	}

	fun pause() {
		mediaPlayer?.also { mp ->
			mp.pause()
			_uiState.value = _uiState.value.copy(isPlaying = false)
			stopProgressUpdates()
		}
	}

	fun seekTo(ms: Int) {
		mediaPlayer?.also { mp ->
			val to = ms.coerceIn(0, mp.duration)
			mp.seekTo(to)
			_uiState.value = _uiState.value.copy(positionMs = to)
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
						_uiState.value = _uiState.value.copy(positionMs = mp.currentPosition)
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
			_uiState.value = PlayerUiState()
		}
	}

	override fun onCleared() {
		if (audioFile != null) cleanupScope.launch { deleteFromCache(audioFile!!) }
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
