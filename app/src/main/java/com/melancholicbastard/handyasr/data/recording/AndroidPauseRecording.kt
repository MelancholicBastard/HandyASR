package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.data.AndroidAudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.PauseRecording
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager

class AndroidPauseRecording : PauseRecording {
    override fun pause() {
        AndroidTimerManager.pauseTimer()
        AndroidAudioRecorderManager.pauseAudioRecording()
    }
}