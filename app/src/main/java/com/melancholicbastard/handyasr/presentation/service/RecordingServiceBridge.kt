package com.melancholicbastard.handyasr.presentation.service

import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingResultProvider
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingRuntimeState
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingStateProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object RecordingServiceBridge : RecordingStateProvider, RecordingResultProvider {
    private val _state = MutableSharedFlow<RecordingRuntimeState>(replay = 0)
    override val state: SharedFlow<RecordingRuntimeState> = _state.asSharedFlow()

    private val _filePath = MutableSharedFlow<String>(replay = 0)
    override val filePath: SharedFlow<String> = _filePath.asSharedFlow()

    suspend fun updateState(newState: RecordingRuntimeState) {
        _state.emit(newState)
    }

    suspend fun updateResult(newResult: String) {
        _filePath.emit(newResult)
    }
}

