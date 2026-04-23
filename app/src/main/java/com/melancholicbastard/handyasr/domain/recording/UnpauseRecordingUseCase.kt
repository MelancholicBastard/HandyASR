package com.melancholicbastard.handyasr.domain.recording

import javax.inject.Inject

class UnpauseRecordingUseCase @Inject constructor(
    private val unpauseRecording: UnpauseRecording
) {
    operator fun invoke() {
        unpauseRecording.unpause()
    }
}