package com.melancholicbastard.handyasr.domain.recording

import javax.inject.Inject

class StartRecordingUseCase @Inject constructor(
    private val startRecording: StartRecording
) {
    suspend operator fun invoke() {
        startRecording.start()
    }
}