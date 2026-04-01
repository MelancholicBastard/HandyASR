package com.melancholicbastard.handyasr.domain.recordingcontrol

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface RecordingStateProvider {
    val state: SharedFlow<RecordingRuntimeState>
}

