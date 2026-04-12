package com.melancholicbastard.handyasr.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    val detail: String
)