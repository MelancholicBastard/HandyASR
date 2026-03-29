package com.melancholicbastard.handyasr.domain.recording

class UnpauseRecordingUseCase(
    private val unpauseRecording: UnpauseRecording
) {
    operator fun invoke() {
        unpauseRecording.unpause()
    }
}