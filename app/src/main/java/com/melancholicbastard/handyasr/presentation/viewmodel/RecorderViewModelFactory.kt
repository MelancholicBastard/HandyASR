package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.melancholicbastard.handyasr.data.permission.AndroidMicrophonePermissionChecker
import com.melancholicbastard.handyasr.data.recording.AndroidAcceptRecording
import com.melancholicbastard.handyasr.data.recording.AndroidPauseRecording
import com.melancholicbastard.handyasr.data.recording.AndroidRejectRecording
import com.melancholicbastard.handyasr.data.recording.AndroidStartRecording
import com.melancholicbastard.handyasr.data.recording.AndroidUnpauseRecording
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionCheckUseCase
import com.melancholicbastard.handyasr.domain.recording.AcceptRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.PauseRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.RejectRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.StartRecordingUseCase
import com.melancholicbastard.handyasr.domain.recording.UnpauseRecordingUseCase
import com.melancholicbastard.handyasr.presentation.App

class RecorderViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appContext = App.instance.applicationContext
        val permissionChecker = AndroidMicrophonePermissionChecker(appContext)
        val checkMicPermissionUseCase = MicrophonePermissionCheckUseCase(permissionChecker)
        val startRecUseCase = StartRecordingUseCase(AndroidStartRecording())
        val pauseRecUseCase = PauseRecordingUseCase(AndroidPauseRecording())
        val unpauseRecUseCase = UnpauseRecordingUseCase(AndroidUnpauseRecording())
        val rejectRecUseCase = RejectRecordingUseCase(AndroidRejectRecording())
        val acceptRecUseCase = AcceptRecordingUseCase(AndroidAcceptRecording())

        return RecorderViewModel(
            checkMicPermission = checkMicPermissionUseCase,
            startRec = startRecUseCase,
            pauseRec = pauseRecUseCase,
            unpauseRec = unpauseRecUseCase,
            rejectRec = rejectRecUseCase,
            acceptRec = acceptRecUseCase
        ) as T
    }
}