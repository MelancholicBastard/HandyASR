package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.data.AndroidAudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.RejectRecording
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager

class AndroidRejectRecording : RejectRecording {
    override suspend fun reject() {
        AndroidAudioRecorderManager.stopAudioRecording(delete = true)
        AndroidTimerManager.stopTimer()
    }
}