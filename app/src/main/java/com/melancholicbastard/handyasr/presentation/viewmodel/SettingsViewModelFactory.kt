package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val appContext = App.instance.applicationContext
        return SettingsViewModel() as T
    }
}