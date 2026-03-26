package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionCheckUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecorderViewModel(
    private val checkMicPermission: MicrophonePermissionCheckUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<RecordScreenUIState>(RecordScreenUIState.IdleUIState)
    val uiState: StateFlow<RecordScreenUIState> = _uiState.asStateFlow()

    private val _requestForPermission = MutableSharedFlow<Unit>(replay = 0)
    val requestForPermission: SharedFlow<Unit> = _requestForPermission.asSharedFlow()

    fun startRecording() {
        if (!checkMicPermission()) {
            viewModelScope.launch { _requestForPermission.emit(Unit) }
        } else {
            _uiState.value = RecordScreenUIState.StartUIState
        }
    }

    fun pauseRecording() {
        _uiState.value = RecordScreenUIState.PauseUIState
    }

    fun unpauseRecording() {
        _uiState.value = RecordScreenUIState.StartUIState
    }

    fun rejectRecord() {
        _uiState.value = RecordScreenUIState.IdleUIState
    }

    fun acceptRecord() { _uiState.value = RecordScreenUIState.RedactUIState }
}

sealed class RecordScreenUIState {
    object IdleUIState: RecordScreenUIState()
    object StartUIState: RecordScreenUIState()
    object PauseUIState: RecordScreenUIState()
    object ProcessUIState: RecordScreenUIState()
    object RedactUIState: RecordScreenUIState()
}

