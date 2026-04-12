package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.data.AndroidAudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.AcceptRecording
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager

class AndroidAcceptRecording : AcceptRecording {
    override suspend fun accept(): String {
        val file = AndroidAudioRecorderManager.stopAudioRecording(delete = false)
        AndroidTimerManager.stopTimer()
        return file!!
    }
}