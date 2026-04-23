package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RecorderViewModelFactory(
    private val onOpenEditorForNewRecord: (String) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val appContext = App.instance.applicationContext
//        val permissionChecker = AndroidMicrophonePermissionChecker(appContext)
//        val checkMicPermissionUseCase = MicrophonePermissionCheckUseCase(permissionChecker)
//        val observeRecordingStateUseCase = ObserveRecordingStateUseCase(RecordingServiceBridge)
//        val observeRecordingResultUseCase = ObserveRecordingResultUseCase(RecordingServiceBridge)
//        val sendRecordingCommandUseCase = SendRecordingCommandUseCase(AndroidRecordingServiceCommandSender())
//
//        return RecorderViewModel(
//            checkMicPermission = checkMicPermissionUseCase,
//            observeRecordingState = observeRecordingStateUseCase,
//            sendRecordingCommand = sendRecordingCommandUseCase,
//            observeRecordingResult = observeRecordingResultUseCase,
//            onOpenEditorForNewRecord = onOpenEditorForNewRecord
//        ) as T
        throw UnsupportedOperationException("Use Hilt hiltViewModel() instead of RecorderViewModelFactory")
    }
}