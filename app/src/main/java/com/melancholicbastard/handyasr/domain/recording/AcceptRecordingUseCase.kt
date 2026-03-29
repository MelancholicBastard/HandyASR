package com.melancholicbastard.handyasr.domain.recording

import java.io.File

class AcceptRecordingUseCase(
    private val acceptRecording: AcceptRecording
) {
    suspend operator fun invoke(): File {
        return acceptRecording.accept()
    }
}