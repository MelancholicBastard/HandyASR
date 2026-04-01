package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.data.AndroidAudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.StartRecording
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager

class AndroidStartRecording : StartRecording {
    override suspend fun start() {
        AndroidTimerManager.startTimer()
        AndroidAudioRecorderManager.startAudioRecording()
    }
}