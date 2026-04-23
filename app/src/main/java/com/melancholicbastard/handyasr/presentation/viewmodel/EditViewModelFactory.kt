package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditViewModelFactory(
    private val isNewRecord: Boolean,
    private val entity: String,
    private val onNodeSaved: () -> Unit,
    private val onBackClick: () -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val recordingsDir = App.recordingsDir
//        val androidReplaceFromCache = AndroidReplaceFromCache(recordingsDir)
//        val replaceFromCache = ReplaceFromCacheUseCase(androidReplaceFromCache)
//        val androidDeleteFromCache = AndroidDeleteFromCache()
//        val deleteFromCache = DeleteFromCacheUseCase(androidDeleteFromCache)
//
//        val appContainer = App.instance.appContainer
//        val decodeAudioUseCase = appContainer.decodeAudioUseCase
//        val addNodeUseCase = appContainer.addNodeUseCase
//        val updateNodeUseCase = appContainer.updateNodeUseCase
//        val getNodeByIdUseCase = appContainer.getNodeByIdUseCase
//        return EditViewModel(
//            isNewRecord = isNewRecord,
//            entity = entity,
//            replaceFromCache = replaceFromCache,
//            deleteFromCache = deleteFromCache,
//            decodeAudioUseCase = decodeAudioUseCase,
//            addNodeUseCase = addNodeUseCase,
//            updateNodeUseCase = updateNodeUseCase,
//            getNodeByIdUseCase = getNodeByIdUseCase,
//            onNodeSaved = onNodeSaved,
//            onBackClick = onBackClick
//        ) as T
        throw UnsupportedOperationException("Use Hilt hiltViewModel() instead of EditViewModelFactory")
    }
}