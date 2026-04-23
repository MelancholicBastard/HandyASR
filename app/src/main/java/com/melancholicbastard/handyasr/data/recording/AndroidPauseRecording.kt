package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.domain.AudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.PauseRecording
import com.melancholicbastard.handyasr.domain.TimerManager
import javax.inject.Inject

class AndroidPauseRecording @Inject constructor(
    private val timerManager: TimerManager,
    private val audioRecorderManager: AudioRecorderManager
) : PauseRecording {
    override fun pause() {
        timerManager.pauseTimer()
        audioRecorderManager.pauseAudioRecording()
    }
}