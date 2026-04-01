package com.melancholicbastard.handyasr.domain.recording

class RejectRecordingUseCase(
    private val rejectRecording : RejectRecording
) {
    suspend operator fun invoke() {
        rejectRecording.reject()
    }
}