package com.melancholicbastard.handyasr.domain

import java.io.File

interface AudioRecorderManager {
    suspend fun startAudioRecording()
    fun pauseAudioRecording()
    fun resumeAudioRecording()
    suspend fun stopAudioRecording(delete: Boolean): File?
}