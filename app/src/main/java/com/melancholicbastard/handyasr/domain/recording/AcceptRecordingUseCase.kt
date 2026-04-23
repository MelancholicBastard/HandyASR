package com.melancholicbastard.handyasr.domain.recording

import javax.inject.Inject

class AcceptRecordingUseCase @Inject constructor(
    private val acceptRecording: AcceptRecording
) {
    suspend operator fun invoke(): String {
        return acceptRecording.accept()
    }
}