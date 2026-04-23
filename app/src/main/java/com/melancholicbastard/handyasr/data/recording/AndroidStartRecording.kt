package com.melancholicbastard.handyasr.data.recording

import com.melancholicbastard.handyasr.domain.AudioRecorderManager
import com.melancholicbastard.handyasr.domain.recording.StartRecording
import com.melancholicbastard.handyasr.domain.TimerManager
import javax.inject.Inject

class AndroidStartRecording @Inject constructor(
    private val timerManager: TimerManager,
    private val audioRecorderManager: AudioRecorderManager
) : StartRecording {
    override suspend fun start() {
        timerManager.startTimer()
        audioRecorderManager.startAudioRecording()
    }
}