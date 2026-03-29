package com.melancholicbastard.handyasr.domain.recording

import java.io.File

interface AcceptRecording {
    suspend fun accept(): File
}