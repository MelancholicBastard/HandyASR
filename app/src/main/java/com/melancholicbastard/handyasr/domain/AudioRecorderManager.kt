package com.melancholicbastard.handyasr.domain

interface AudioRecorderManager {
    suspend fun startAudioRecording()
    fun pauseAudioRecording()
    fun resumeAudioRecording()
    suspend fun stopAudioRecording(delete: Boolean): String?
}