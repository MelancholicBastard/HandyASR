package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.melancholicbastard.handyasr.data.permission.AndroidMicrophonePermissionChecker
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionCheckUseCase
import com.melancholicbastard.handyasr.domain.recordingcontrol.ObserveRecordingResultUseCase
import com.melancholicbastard.handyasr.domain.recordingcontrol.ObserveRecordingStateUseCase
import com.melancholicbastard.handyasr.domain.recordingcontrol.SendRecordingCommandUseCase
import com.melancholicbastard.handyasr.presentation.App
import com.melancholicbastard.handyasr.presentation.service.AndroidRecordingServiceCommandSender
import com.melancholicbastard.handyasr.presentation.service.RecordingServiceBridge

class RecorderViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appContext = App.instance.applicationContext
        val permissionChecker = AndroidMicrophonePermissionChecker(appContext)
        val checkMicPermissionUseCase = MicrophonePermissionCheckUseCase(permissionChecker)
        val observeRecordingStateUseCase = ObserveRecordingStateUseCase(RecordingServiceBridge)
        val observeRecordingResultUseCase = ObserveRecordingResultUseCase(RecordingServiceBridge)
        val sendRecordingCommandUseCase = SendRecordingCommandUseCase(AndroidRecordingServiceCommandSender())

        return RecorderViewModel(
            checkMicPermission = checkMicPermissionUseCase,
            observeRecordingState = observeRecordingStateUseCase,
            sendRecordingCommand = sendRecordingCommandUseCase,
            observeRecordingResult = observeRecordingResultUseCase,
        ) as T
    }
}