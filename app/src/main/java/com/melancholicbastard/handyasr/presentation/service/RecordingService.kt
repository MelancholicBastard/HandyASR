package com.melancholicbastard.handyasr.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.melancholicbastard.handyasr.domain.recording.AcceptRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.PauseRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.RejectRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.StartRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.UnpauseRecordingUseCase
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingRuntimeState
import com.melancholicbastard.handyasr.presentation.AndroidTimerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordingService : Service() {
    companion object {
        private const val TAG = "RecordingService"
        const val CHANNEL_ID = "recording_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "com.melancholicbastard.handyasr.action.START"
        const val ACTION_PAUSE = "com.melancholicbastard.handyasr.action.PAUSE"
        const val ACTION_UNPAUSE = "com.melancholicbastard.handyasr.action.UNPAUSE"
        const val ACTION_ACCEPT = "com.melancholicbastard.handyasr.action.ACCEPT"
        const val ACTION_REJECT = "com.melancholicbastard.handyasr.action.REJECT"
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var elapsedCollectorJob: Job? = null

    @Inject
    lateinit var startRecUseCase: StartRecordingUseCase

    @Inject
    lateinit var pauseRecUseCase: PauseRecordingUseCase

    @Inject
    lateinit var unpauseRecUseCase: UnpauseRecordingUseCase

    @Inject
    lateinit var rejectRecUseCase: RejectRecordingUseCase

    @Inject
    lateinit var acceptRecUseCase: AcceptRecordingUseCase

    @Inject
    lateinit var recordingServiceBridge: RecordingServiceBridge

    @Inject
    lateinit var timerManager: AndroidTimerManager

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText("00:00")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .setColor(Color.Blue.toArgb())
            .setOnlyAlertOnce(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startRecording()
            ACTION_PAUSE -> pauseRecording()
            ACTION_UNPAUSE -> unpauseRecording()
            ACTION_REJECT -> rejectRecording()
            ACTION_ACCEPT -> acceptRecording()
        }

        return START_STICKY
    }

    private fun acceptRecording() {
//        updateRuntimeState(RecordingRuntimeState.PROCESSING)
        scope.launch {
            runCatching { acceptRecUseCase() }
                .onSuccess { file ->
                    Log.d(TAG, "Accepted file: ${file}")
                    recordingServiceBridge.updateResult(file)
                    stopServiceAndResetState()
                }
                .onFailure { throwable ->
                    Log.e(TAG, "Accept failed", throwable)
                    updateRuntimeState(RecordingRuntimeState.ERROR)
                }
        }
    }

    private fun rejectRecording() {
        scope.launch {
            runCatching { rejectRecUseCase() }
                .onSuccess { stopServiceAndResetState() }
                .onFailure { throwable ->
                    Log.e(TAG, "Reject failed", throwable)
                    updateRuntimeState(RecordingRuntimeState.ERROR)
                }
        }
    }

    private fun unpauseRecording() {
        runCatching {
            unpauseRecUseCase()
            updateRuntimeState(RecordingRuntimeState.RECORDING)
            updateNotification(RecordingRuntimeState.RECORDING)
        }.onFailure { throwable ->
            Log.e(TAG, "Unpause failed", throwable)
            updateRuntimeState(RecordingRuntimeState.ERROR)
        }
    }

    private fun pauseRecording() {
        runCatching {
            pauseRecUseCase()
            updateRuntimeState(RecordingRuntimeState.PAUSED)
            updateNotification(RecordingRuntimeState.PAUSED)
        }.onFailure { throwable ->
            Log.e(TAG, "Pause failed", throwable)
            updateRuntimeState(RecordingRuntimeState.ERROR)
        }
    }

    fun startRecording() {
        createNotificationChannelIfNeeded()
        updateNotification(RecordingRuntimeState.RECORDING)

        elapsedCollectorJob?.cancel()
        elapsedCollectorJob = scope.launch {
            timerManager.elapsedMs
                .map { time ->
                    val seconds = (time / 1000) % 60
                    val minutes = time / 60_000
                    String.format("%02d:%02d", minutes, seconds)
                }
                .distinctUntilChanged()
                .collect { text ->
                    notificationBuilder.setContentText(text)
                    startForegroundService()
                }
        }

        updateRuntimeState(RecordingRuntimeState.RECORDING)

        scope.launch {
            runCatching { startRecUseCase() }
                .onFailure { throwable ->
                    Log.e(TAG, "Start failed", throwable)
                    updateRuntimeState(RecordingRuntimeState.ERROR)
                }
        }
    }

    private fun updateRuntimeState(newState: RecordingRuntimeState) {
        scope.launch { recordingServiceBridge.updateState(newState) }
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(
                NOTIFICATION_ID,
                notificationBuilder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun stopServiceAndResetState() {
        updateRuntimeState(RecordingRuntimeState.IDLE)
        elapsedCollectorJob?.cancel()
        elapsedCollectorJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification(state: RecordingRuntimeState) {
        if (state == RecordingRuntimeState.RECORDING) {
            notificationBuilder.setContentTitle("Recording in progress")
        } else {
            notificationBuilder.setContentTitle("Recording paused")
        }
        notificationBuilder.clearActions()

        val toggleAction =
            if (state == RecordingRuntimeState.PAUSED) ACTION_UNPAUSE else ACTION_PAUSE
        val toggleTitle = if (state == RecordingRuntimeState.PAUSED) "Resume" else "Pause"
        val toggleIcon = if (state == RecordingRuntimeState.PAUSED) {
            android.R.drawable.ic_media_play
        } else {
            android.R.drawable.ic_media_pause
        }

        notificationBuilder.addAction(
            toggleIcon,
            toggleTitle,
            createActionPendingIntent(toggleAction, 101)
        )
        notificationBuilder.addAction(
            android.R.drawable.ic_menu_add,
            "Accept",
            createActionPendingIntent(ACTION_ACCEPT, 102)
        )
        notificationBuilder.addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Reject",
            createActionPendingIntent(ACTION_REJECT, 103)
        )

        startForegroundService()
    }

    private fun createActionPendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, RecordingService::class.java).apply { this.action = action }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getService(this, requestCode, intent, flags)
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recording",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        updateRuntimeState(RecordingRuntimeState.IDLE)
        elapsedCollectorJob?.cancel()
        elapsedCollectorJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        scope.coroutineContext.cancelChildren()
        super.onDestroy()
    }
}