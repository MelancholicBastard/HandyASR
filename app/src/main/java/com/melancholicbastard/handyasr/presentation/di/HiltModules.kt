package com.melancholicbastard.handyasr.presentation.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.melancholicbastard.handyasr.BuildConfig
import com.melancholicbastard.handyasr.data.AndroidAudioRecorderManager
import com.melancholicbastard.handyasr.data.db.AppDatabase
import com.melancholicbastard.handyasr.data.db.node.NodeDao
import com.melancholicbastard.handyasr.data.db.node.RoomNodeRepository
import com.melancholicbastard.handyasr.data.editor.AndroidDeleteFromCache
import com.melancholicbastard.handyasr.data.editor.AndroidReplaceFromCache
import com.melancholicbastard.handyasr.data.permission.AndroidMicrophonePermissionChecker
import com.melancholicbastard.handyasr.data.recording.AndroidAcceptRecording
import com.melancholicbastard.handyasr.data.recording.AndroidPauseRecording
import com.melancholicbastard.handyasr.data.recording.AndroidRejectRecording
import com.melancholicbastard.handyasr.data.recording.AndroidStartRecording
import com.melancholicbastard.handyasr.data.recording.AndroidUnpauseRecording
import com.melancholicbastard.handyasr.data.remote.DecodeApi
import com.melancholicbastard.handyasr.data.remote.RetrofitDecodeRepository
import com.melancholicbastard.handyasr.domain.AudioRecorderManager
import com.melancholicbastard.handyasr.domain.TimerManager
import com.melancholicbastard.handyasr.domain.decode.DecodeRepository
import com.melancholicbastard.handyasr.domain.editor.DeleteFromCache
import com.melancholicbastard.handyasr.domain.editor.ReplaceFromCache
import com.melancholicbastard.handyasr.domain.node.NodeRepository
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionChecker
import com.melancholicbastard.handyasr.domain.recording.AcceptRecording
import com.melancholicbastard.handyasr.domain.recording.PauseRecording
import com.melancholicbastard.handyasr.domain.recording.RejectRecording
import com.melancholicbastard.handyasr.domain.recording.StartRecording
import com.melancholicbastard.handyasr.domain.recording.UnpauseRecording
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingCommandSender
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingResultProvider
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingStateProvider
import com.melancholicbastard.handyasr.presentation.service.AndroidRecordingServiceCommandSender
import com.melancholicbastard.handyasr.presentation.service.RecordingServiceBridge
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager
import com.melancholicbastard.handyasr.presentation.di.annotation.RecordingsDir
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {
    @Binds
    abstract fun bindDecodeRepository(impl: RetrofitDecodeRepository): DecodeRepository

    @Binds
    abstract fun bindNodeRepository(impl: RoomNodeRepository): NodeRepository

    @Binds
    abstract fun bindMicrophonePermissionChecker(impl: AndroidMicrophonePermissionChecker): MicrophonePermissionChecker

    @Binds
    abstract fun bindAudioRecorderManager(impl: AndroidAudioRecorderManager): AudioRecorderManager

    @Binds
    abstract fun bindTimerManager(impl: AndroidTimerManager): TimerManager

    @Binds
    abstract fun bindStartRecording(impl: AndroidStartRecording): StartRecording

    @Binds
    abstract fun bindPauseRecording(impl: AndroidPauseRecording): PauseRecording

    @Binds
    abstract fun bindUnpauseRecording(impl: AndroidUnpauseRecording): UnpauseRecording

    @Binds
    abstract fun bindAcceptRecording(impl: AndroidAcceptRecording): AcceptRecording

    @Binds
    abstract fun bindRejectRecording(impl: AndroidRejectRecording): RejectRecording

    @Binds
    abstract fun bindReplaceFromCache(impl: AndroidReplaceFromCache): ReplaceFromCache

    @Binds
    abstract fun bindDeleteFromCache(impl: AndroidDeleteFromCache): DeleteFromCache

    @Binds
    abstract fun bindRecordingCommandSender(impl: AndroidRecordingServiceCommandSender): RecordingCommandSender

    @Binds
    abstract fun bindRecordingStateProvider(impl: RecordingServiceBridge): RecordingStateProvider

    @Binds
    abstract fun bindRecordingResultProvider(impl: RecordingServiceBridge): RecordingResultProvider
}

@Module
@InstallIn(SingletonComponent::class)
object AppProvidersModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val baseUrl = normalizeBaseUrl(BuildConfig.BASE_URL)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideDecodeApi(retrofit: Retrofit): DecodeApi = retrofit.create(DecodeApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "handy_asr.db"
        ).addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideNodeDao(database: AppDatabase): NodeDao = database.nodeDao()

    @Provides
    @Singleton
    @RecordingsDir
    fun provideRecordingsDir(@ApplicationContext context: Context): File {
        return File(context.filesDir, "recordings").apply { mkdirs() }
    }

    private fun normalizeBaseUrl(baseUrl: String): String {
        val trimmed = baseUrl.trim()
        require(trimmed.isNotEmpty()) { "BASE_URL is empty" }
        return if (trimmed.endsWith('/')) trimmed else "$trimmed/"
    }
}
