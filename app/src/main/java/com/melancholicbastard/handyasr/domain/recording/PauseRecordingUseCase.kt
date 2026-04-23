package com.melancholicbastard.handyasr.domain.recording

import javax.inject.Inject

class PauseRecordingUseCase @Inject constructor(
    private val pauseRecording: PauseRecording
) {
    operator fun invoke() {
        pauseRecording.pause()
    }
}