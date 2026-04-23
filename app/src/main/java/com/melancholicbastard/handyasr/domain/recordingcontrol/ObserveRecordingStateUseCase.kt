package com.melancholicbastard.handyasr.domain.recordingcontrol

import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ObserveRecordingStateUseCase @Inject constructor(
    private val provider: RecordingStateProvider
) {
    operator fun invoke(): SharedFlow<RecordingRuntimeState> = provider.state
}

