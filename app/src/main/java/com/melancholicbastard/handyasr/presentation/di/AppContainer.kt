package com.melancholicbastard.handyasr.presentation.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.melancholicbastard.handyasr.BuildConfig
import com.melancholicbastard.handyasr.data.remote.DecodeApi
import com.melancholicbastard.handyasr.data.remote.RetrofitDecodeRepository
import com.melancholicbastard.handyasr.domain.decode.DecodeAudioUseCase
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class AppContainer {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(normalizeBaseUrl(BuildConfig.BASE_URL))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
    }

    private val decodeApi: DecodeApi by lazy {
        retrofit.create(DecodeApi::class.java)
    }

    private val decodeRepository by lazy {
        RetrofitDecodeRepository(decodeApi, json)
    }

    val decodeAudioUseCase: DecodeAudioUseCase by lazy {
        DecodeAudioUseCase(decodeRepository)
    }

    private fun normalizeBaseUrl(baseUrl: String): String {
        val trimmed = baseUrl.trim()
        require(trimmed.isNotEmpty()) { "BASE_URL is empty" }
        return if (trimmed.endsWith('/')) trimmed else "$trimmed/"
    }
}

