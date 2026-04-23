package com.melancholicbastard.handyasr.domain.recording

import javax.inject.Inject

class RejectRecordingUseCase @Inject constructor(
    private val rejectRecording : RejectRecording
) {
    suspend operator fun invoke() {
        rejectRecording.reject()
    }
}