package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.domain.AudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.RejectRecording
import com.melancholicbastard.handyasr.domain.TimerManager
import javax.inject.Inject

class AndroidRejectRecording @Inject constructor(
    private val audioRecorderManager: AudioRecorderManager,
    private val timerManager: TimerManager
) : RejectRecording {
    override suspend fun reject() {
        audioRecorderManager.stopAudioRecording(delete = true)
        timerManager.stopTimer()
    }
}