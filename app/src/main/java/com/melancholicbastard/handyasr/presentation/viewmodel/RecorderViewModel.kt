package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionCheckUseCase
import com.melancholicbastard.handyasr.domain.recordingcontrol.ObserveRecordingResultUseCase
import com.melancholicbastard.handyasr.domain.recordingcontrol.ObserveRecordingStateUseCase
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingCommand
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingRuntimeState
import com.melancholicbastard.handyasr.domain.recordingcontrol.SendRecordingCommandUseCase
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecorderViewModel @Inject constructor(
    private val checkMicPermission: MicrophonePermissionCheckUseCase,
    private val observeRecordingState: ObserveRecordingStateUseCase,
    private val sendRecordingCommand: SendRecordingCommandUseCase,
    private val observeRecordingResult: ObserveRecordingResultUseCase,
    private val timerManager: AndroidTimerManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<RecordScreenUIState>(RecordScreenUIState.IdleUIState)
    val uiState: StateFlow<RecordScreenUIState> = _uiState.asStateFlow()

    private val _requestForPermission = MutableSharedFlow<Unit>(replay = 0)
    val requestForPermission: SharedFlow<Unit> = _requestForPermission.asSharedFlow()

    private val _navigationEvents = MutableSharedFlow<RecorderNavigationEvent>(replay = 0)
    val navigationEvents: SharedFlow<RecorderNavigationEvent> = _navigationEvents.asSharedFlow()

    val elapsedMs: StateFlow<Long> = timerManager.elapsedMs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    init {
        viewModelScope.launch {
            observeRecordingState().collect { runtimeState ->
                _uiState.value = runtimeState.toUiState()
            }
        }
        viewModelScope.launch {
            observeRecordingResult().collect { filePath ->
                _navigationEvents.emit(RecorderNavigationEvent.OpenEditorForNewRecord(filePath))
            }
        }
    }

    fun startRecording() {
        if (!checkMicPermission()) {
            viewModelScope.launch { _requestForPermission.emit(Unit) }
        } else {
            sendRecordingCommand(RecordingCommand.START)
        }
    }

    fun pauseRecording() {
        sendRecordingCommand(RecordingCommand.PAUSE)
    }

    fun unpauseRecording() {
        sendRecordingCommand(RecordingCommand.UNPAUSE)
    }

    fun rejectRecord() {
        sendRecordingCommand(RecordingCommand.REJECT)
    }

    fun acceptRecord() {
        _uiState.value = RecordScreenUIState.ProcessUIState
        sendRecordingCommand(RecordingCommand.ACCEPT)
    }

    private fun RecordingRuntimeState.toUiState(): RecordScreenUIState {
        return when (this) {
            RecordingRuntimeState.IDLE -> RecordScreenUIState.IdleUIState
            RecordingRuntimeState.RECORDING -> RecordScreenUIState.StartUIState
            RecordingRuntimeState.PAUSED -> RecordScreenUIState.PauseUIState
            RecordingRuntimeState.PROCESSING -> RecordScreenUIState.ProcessUIState
            RecordingRuntimeState.ERROR -> RecordScreenUIState.IdleUIState
        }
    }
}

sealed class RecorderNavigationEvent {
    data class OpenEditorForNewRecord(val filePath: String) : RecorderNavigationEvent()
}

sealed class RecordScreenUIState {
    object IdleUIState : RecordScreenUIState()
    object StartUIState : RecordScreenUIState()
    object PauseUIState : RecordScreenUIState()
    object ProcessUIState : RecordScreenUIState()
}
