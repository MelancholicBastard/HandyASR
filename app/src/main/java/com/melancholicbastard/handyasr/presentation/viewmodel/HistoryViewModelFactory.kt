package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HistoryViewModelFactory(
    private val onOpenEditorForExistingRecord: (String) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val appContext = App.instance.applicationContext
        return HistoryViewModel(
            onOpenEditorForExistingRecord = onOpenEditorForExistingRecord
        ) as T
    }
}