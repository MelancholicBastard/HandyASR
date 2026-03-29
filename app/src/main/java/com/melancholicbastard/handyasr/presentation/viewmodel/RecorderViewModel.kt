package com.melancholicbastard.handyasr.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionCheckUseCase
import com.melancholicbastard.handyasr.domain.recording.AcceptRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.PauseRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.RejectRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.StartRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.UnpauseRecordingUseCase
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecorderViewModel(
    private val checkMicPermission: MicrophonePermissionCheckUseCase,
    private val startRec: StartRecordingUseCase,
    private val pauseRec: PauseRecordingUseCase,
    private val unpauseRec: UnpauseRecordingUseCase,
    private val rejectRec: RejectRecordingUseCase,
    private val acceptRec: AcceptRecordingUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<RecordScreenUIState>(RecordScreenUIState.IdleUIState)
    val uiState: StateFlow<RecordScreenUIState> = _uiState.asStateFlow()

    private val _requestForPermission = MutableSharedFlow<Unit>(replay = 0)
    val requestForPermission: SharedFlow<Unit> = _requestForPermission.asSharedFlow()

    val elapsedMs: StateFlow<Long> = AndroidTimerManager.elapsedMs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    fun startRecording() {
        if (!checkMicPermission()) {
            viewModelScope.launch { _requestForPermission.emit(Unit) }
        } else {
            _uiState.value = RecordScreenUIState.StartUIState
            viewModelScope.launch {
                startRec()
            }
//            AndroidTimerManager.startTimer()
//            viewModelScope.launch { AndroidAudioRecorderManager.startAudioRecording() }
        }
    }

    fun pauseRecording() {
        pauseRec()
//        AndroidTimerManager.pauseTimer()
//        AndroidAudioRecorderManager.pauseAudioRecording()
        _uiState.value = RecordScreenUIState.PauseUIState
    }

    fun unpauseRecording() {
        unpauseRec()
//        AndroidTimerManager.resumeTimer()
//        AndroidAudioRecorderManager.resumeAudioRecording()
        _uiState.value = RecordScreenUIState.StartUIState
    }

    fun rejectRecord() {
        viewModelScope.launch {
            rejectRec()
//            AndroidAudioRecorderManager.stopAudioRecording(delete = true)
//            AndroidTimerManager.stopTimer()
            _uiState.value = RecordScreenUIState.IdleUIState
        }
    }

    fun acceptRecord() {
        viewModelScope.launch {
            val file = acceptRec()
//            val file = AndroidAudioRecorderManager.stopAudioRecording(delete = false)
//            AndroidTimerManager.stopTimer()
            _uiState.value = RecordScreenUIState.IdleUIState
//            _uiState.value = RecordScreenUIState.RedactUIState
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            rejectRec()
//            AndroidAudioRecorderManager.stopAudioRecording(delete = true)
        }
        super.onCleared()
    }
}

sealed class RecordScreenUIState {
    object IdleUIState : RecordScreenUIState()
    object StartUIState : RecordScreenUIState()
    object PauseUIState : RecordScreenUIState()
    object ProcessUIState : RecordScreenUIState()
    object RedactUIState : RecordScreenUIState()
}
