package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.melancholicbastard.handyasr.presentation.App

class HistoryViewModelFactory(
    private val onOpenEditorForExistingRecord: (String) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appContainer = App.instance.appContainer
        return HistoryViewModel(
            searchNodesByUseCase = appContainer.searchNodesByUseCase,
            deleteNodeByIdUseCase = appContainer.deleteNodeByIdUseCase,
            deleteAllNodesUseCase = appContainer.deleteAllNodesUseCase,
            getAllNodesUseCase = appContainer.getAllNodesUseCase,
            onOpenEditorForExistingRecord = onOpenEditorForExistingRecord
        ) as T
    }
}