package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.data.AndroidAudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.UnpauseRecording
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager

class AndroidUnpauseRecording : UnpauseRecording {
    override fun unpause() {
        AndroidTimerManager.resumeTimer()
        AndroidAudioRecorderManager.resumeAudioRecording()
    }
}