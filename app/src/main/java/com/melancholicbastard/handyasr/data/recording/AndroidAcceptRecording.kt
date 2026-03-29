package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.data.AndroidAudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.AcceptRecording
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager
import java.io.File

class AndroidAcceptRecording : AcceptRecording {
    override suspend fun accept(): File {
        val file = AndroidAudioRecorderManager.stopAudioRecording(delete = false)
        AndroidTimerManager.stopTimer()
        return file!!
    }
}