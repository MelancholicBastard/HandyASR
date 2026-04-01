package com.melancholicbastard.handyasr.presentation.service

import android.util.Log
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingResultProvider
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingRuntimeState
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingStateProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

object RecordingServiceBridge : RecordingStateProvider, RecordingResultProvider {
    private val _state = MutableSharedFlow<RecordingRuntimeState>(replay = 0)
    override val state: SharedFlow<RecordingRuntimeState> = _state.asSharedFlow()

    private val _file = MutableSharedFlow<File>(replay = 0)
    override val file: SharedFlow<File> = _file.asSharedFlow()

    suspend fun updateState(newState: RecordingRuntimeState) {
        _state.emit(newState)
    }

    suspend fun updateResult(newResult: File) {
        _file.emit(newResult)
    }
}

