package com.melancholicbastard.handyasr.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionCheckUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RecorderViewModel(
    private val checkMicPermission: MicrophonePermissionCheckUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<RecordScreenUIState>(RecordScreenUIState.IdleUIState)
    val uiState: StateFlow<RecordScreenUIState> = _uiState.asStateFlow()

    private val _requestForPermission = MutableSharedFlow<Unit>(replay = 0)
    val requestForPermission: SharedFlow<Unit> = _requestForPermission.asSharedFlow()

    private val _elapsedMs = MutableStateFlow(0L)
    val elapsedMs: StateFlow<Long> = _elapsedMs.asStateFlow()

    private var tickerJob: Job? = null
    private var startTimeMs: Long = 0L
    private var accumulatedPauseMs: Long = 0L

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val now = System.currentTimeMillis()
                val elapsed = accumulatedPauseMs + now - startTimeMs
                _elapsedMs.value = if (elapsed >= 0L) elapsed else 0L
                delay(16L)
                Log.d("Timer", "${_elapsedMs.value}")
            }
        }
    }

    private fun startTimer() {
        startTimeMs = System.currentTimeMillis()
        accumulatedPauseMs = 0L
        _elapsedMs.value = 0L
        startTicker()
    }

    private fun pauseTimer() {
        if (tickerJob?.isActive == true) {
            tickerJob?.cancel()
            tickerJob = null
        }
    }

    private fun resumeTimer() {
        startTimeMs = System.currentTimeMillis()
        accumulatedPauseMs = _elapsedMs.value
        startTicker()
    }

    private fun stopTimer() {
        tickerJob?.cancel()
        tickerJob = null
        startTimeMs = 0L
        accumulatedPauseMs = 0L
        _elapsedMs.value = 0L
    }

    fun startRecording() {
        if (!checkMicPermission()) {
            viewModelScope.launch { _requestForPermission.emit(Unit) }
        } else {
            _uiState.value = RecordScreenUIState.StartUIState
            startTimer()
        }
    }

    fun pauseRecording() {
        pauseTimer()
        _uiState.value = RecordScreenUIState.PauseUIState
    }

    fun unpauseRecording() {
        resumeTimer()
        Log.d("Timer", "isActive 111: ${tickerJob?.isActive}")
        _uiState.value = RecordScreenUIState.StartUIState
    }

    fun rejectRecord() {
        stopTimer()
        _uiState.value = RecordScreenUIState.IdleUIState
    }

    fun acceptRecord() {
        stopTimer()
        _uiState.value = RecordScreenUIState.IdleUIState
//        _uiState.value = RecordScreenUIState.RedactUIState
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
    }
}

sealed class RecordScreenUIState {
    object IdleUIState : RecordScreenUIState()
    object StartUIState : RecordScreenUIState()
    object PauseUIState : RecordScreenUIState()
    object ProcessUIState : RecordScreenUIState()
    object RedactUIState : RecordScreenUIState()
}
