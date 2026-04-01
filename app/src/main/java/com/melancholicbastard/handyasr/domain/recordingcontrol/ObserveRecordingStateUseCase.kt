package com.melancholicbastard.handyasr.domain.recordingcontrol

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class ObserveRecordingStateUseCase(
    private val provider: RecordingStateProvider
) {
    operator fun invoke(): SharedFlow<RecordingRuntimeState> = provider.state
}

