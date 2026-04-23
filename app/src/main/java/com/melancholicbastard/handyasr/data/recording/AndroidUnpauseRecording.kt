package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.domain.AudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.UnpauseRecording
import com.melancholicbastard.handyasr.domain.TimerManager
import javax.inject.Inject

class AndroidUnpauseRecording @Inject constructor(
    private val timerManager: TimerManager,
    private val audioRecorderManager: AudioRecorderManager
) : UnpauseRecording {
    override fun unpause() {
        timerManager.resumeTimer()
        audioRecorderManager.resumeAudioRecording()
    }
}