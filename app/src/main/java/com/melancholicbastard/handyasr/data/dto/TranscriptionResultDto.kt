package com.melancholicbastard.handyasr.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranscriptionResultDto(
    val text: String
)