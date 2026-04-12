package com.melancholicbastard.handyasr.domain.decode

import java.io.File

interface DecodeRepository {
    suspend fun decodeAudio(file: File): DecodeResult<String>
}

