package com.melancholicbastard.handyasr.domain.recording

interface AcceptRecording {
    suspend fun accept(): String
}