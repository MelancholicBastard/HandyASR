package com.melancholicbastard.handyasr.domain.recording

class PauseRecordingUseCase(
    private val pauseRecording: PauseRecording
) {
    operator fun invoke() {
        pauseRecording.pause()
    }
}