package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.domain.AudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.AcceptRecording
import com.melancholicbastard.handyasr.domain.TimerManager
import javax.inject.Inject

class AndroidAcceptRecording @Inject constructor(
    private val audioRecorderManager: AudioRecorderManager,
    private val timerManager: TimerManager
) : AcceptRecording {
    override suspend fun accept(): String {
        val file = audioRecorderManager.stopAudioRecording(delete = false)
        timerManager.stopTimer()
        return file!!
    }
}