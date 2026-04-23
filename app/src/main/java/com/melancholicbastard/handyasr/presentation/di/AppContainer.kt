package com.melancholicbastard.handyasr.presentation.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.melancholicbastard.handyasr.BuildConfig
import com.melancholicbastard.handyasr.data.db.AppDatabase
import com.melancholicbastard.handyasr.data.db.node.RoomNodeRepository
import com.melancholicbastard.handyasr.data.remote.DecodeApi
import com.melancholicbastard.handyasr.data.remote.RetrofitDecodeRepository
import com.melancholicbastard.handyasr.domain.decode.DecodeAudioUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.AddNodeUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.DeleteAllNodesUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.DeleteNodeByIdUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.GetAllNodesUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.GetNodeByIdUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.SearchNodesByUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.UpdateNodeUseCase
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

class AppContainer(
    private val context: Context
) {

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

    private val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    private val nodeRepository by lazy {
        RoomNodeRepository(appDatabase.nodeDao())
    }

    val decodeAudioUseCase: DecodeAudioUseCase by lazy {
        DecodeAudioUseCase(decodeRepository)
    }

    val addNodeUseCase: AddNodeUseCase by lazy {
        AddNodeUseCase(nodeRepository)
    }

    val updateNodeUseCase: UpdateNodeUseCase by lazy {
        UpdateNodeUseCase(nodeRepository)
    }

    val deleteNodeByIdUseCase: DeleteNodeByIdUseCase by lazy {
        DeleteNodeByIdUseCase(nodeRepository)
    }

    val searchNodesByUseCase: SearchNodesByUseCase by lazy {
        SearchNodesByUseCase(nodeRepository)
    }

    val getNodeByIdUseCase: GetNodeByIdUseCase by lazy {
        GetNodeByIdUseCase(nodeRepository)
    }

    val deleteAllNodesUseCase: DeleteAllNodesUseCase by lazy {
        DeleteAllNodesUseCase(nodeRepository, File(context.filesDir, "recordings").apply { mkdirs() })
    }

    val getAllNodesUseCase: GetAllNodesUseCase by lazy {
        GetAllNodesUseCase(nodeRepository)
    }

    private fun normalizeBaseUrl(baseUrl: String): String {
        val trimmed = baseUrl.trim()
        require(trimmed.isNotEmpty()) { "BASE_URL is empty" }
        return if (trimmed.endsWith('/')) trimmed else "$trimmed/"
    }
}

