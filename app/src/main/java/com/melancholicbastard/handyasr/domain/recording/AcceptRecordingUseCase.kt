package com.melancholicbastard.handyasr.domain.recording

class AcceptRecordingUseCase(
    private val acceptRecording: AcceptRecording
) {
    suspend operator fun invoke(): String {
        return acceptRecording.accept()
    }
}