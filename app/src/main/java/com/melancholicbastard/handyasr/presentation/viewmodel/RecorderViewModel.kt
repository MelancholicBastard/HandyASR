package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class RecorderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<RecordScreenUIState>(RecordScreenUIState.StartUIState)
    val uiState: StateFlow<RecordScreenUIState> = _uiState.asStateFlow()


    fun startRecording() {
        _uiState.value = RecordScreenUIState.StartUIState
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
