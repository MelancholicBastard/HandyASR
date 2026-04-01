package com.melancholicbastard.handyasr.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.core.app.NotificationCompat
import com.melancholicbastard.handyasr.data.recording.AndroidAcceptRecording
import com.melancholicbastard.handyasr.data.recording.AndroidPauseRecording
import com.melancholicbastard.handyasr.data.recording.AndroidRejectRecording
import com.melancholicbastard.handyasr.data.recording.AndroidStartRecording
import com.melancholicbastard.handyasr.data.recording.AndroidUnpauseRecording
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingRuntimeState
import com.melancholicbastard.handyasr.domain.recording.AcceptRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.PauseRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.RejectRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.StartRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.UnpauseRecordingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class RecordingService(
    private val startRecUseCase : StartRecordingUseCase = StartRecordingUseCase(AndroidStartRecording()),
    private val pauseRecUseCase : PauseRecordingUseCase = PauseRecordingUseCase(AndroidPauseRecording()),
    private val unpauseRecUseCase : UnpauseRecordingUseCase = UnpauseRecordingUseCase(AndroidUnpauseRecording()),
    private val rejectRecUseCase : RejectRecordingUseCase = RejectRecordingUseCase(AndroidRejectRecording()),
    private val acceptRecUseCase : AcceptRecordingUseCase = AcceptRecordingUseCase(AndroidAcceptRecording())
) : Service() {
    companion object {
        private const val TAG = "RecordingService"
        const val CHANNEL_ID = "recording_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "com.melancholicbastard.handyasr.action.START"
        const val ACTION_PAUSE = "com.melancholicbastard.handyasr.action.PAUSE"
        const val ACTION_UNPAUSE = "com.melancholicbastard.handyasr.action.UNPAUSE"
        const val ACTION_ACCEPT = "com.melancholicbastard.handyasr.action.ACCEPT"
        const val ACTION_REJECT= "com.melancholicbastard.handyasr.action.REJECT"
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent?) = null

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
        updateRuntimeState(RecordingRuntimeState.PROCESSING)
        scope.launch {
            runCatching { acceptRecUseCase() }
                .onSuccess { file ->
                    Log.d(TAG, "Accepted file: ${file.absolutePath}")
                    RecordingServiceBridge.updateResult(file)
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
        startForeground(NOTIFICATION_ID, buildNotification(RecordingRuntimeState.RECORDING))
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
        scope.launch { RecordingServiceBridge.updateState(newState) }
    }

    private fun stopServiceAndResetState() {
        updateRuntimeState(RecordingRuntimeState.IDLE)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification(state: RecordingRuntimeState) {
        notificationManager.notify(NOTIFICATION_ID, buildNotification(state))
    }

    private fun buildNotification(state: RecordingRuntimeState): Notification {
        val toggleAction = if (state == RecordingRuntimeState.PAUSED) ACTION_UNPAUSE else ACTION_PAUSE
        val toggleTitle = if (state == RecordingRuntimeState.PAUSED) "Resume" else "Pause"
        val contentText = if (state == RecordingRuntimeState.PAUSED) {
            "Recording paused"
        } else {
            "Recording in progress"
        }
        val toggleIcon = if (state == RecordingRuntimeState.PAUSED) {
            android.R.drawable.ic_media_play
        } else {
            android.R.drawable.ic_media_pause
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Recording")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .addAction(toggleIcon, toggleTitle, createActionPendingIntent(toggleAction, 101))
            .addAction(android.R.drawable.ic_menu_add, "Accept", createActionPendingIntent(ACTION_ACCEPT, 102))
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Reject", createActionPendingIntent(ACTION_REJECT, 103))
            .build()
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
        scope.coroutineContext.cancelChildren()
        updateRuntimeState(RecordingRuntimeState.IDLE)
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }
}