package com.melancholicbastard.handyasr.data.remote

import com.melancholicbastard.handyasr.data.dto.TranscriptionResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DecodeApi {
    @Multipart
    @POST("decode")
    suspend fun decode(
        @Part file: MultipartBody.Part
    ): TranscriptionResponseDto
}