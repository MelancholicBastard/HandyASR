package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.melancholicbastard.handyasr.data.permission.AndroidMicrophonePermissionChecker
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionCheckUseCase
import com.melancholicbastard.handyasr.presentation.App

class RecorderViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appContext = App.instance.applicationContext
        val permissionChecker = AndroidMicrophonePermissionChecker(appContext)
        val useCase = MicrophonePermissionCheckUseCase(permissionChecker)

        return RecorderViewModel(
            checkMicPermission = useCase
        ) as T
    }
}