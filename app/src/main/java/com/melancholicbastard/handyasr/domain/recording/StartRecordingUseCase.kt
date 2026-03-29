package com.melancholicbastard.handyasr.domain.recording

class StartRecordingUseCase(
    private val startRecording: StartRecording
) {
    suspend operator fun invoke() {
        startRecording.start()
    }
}