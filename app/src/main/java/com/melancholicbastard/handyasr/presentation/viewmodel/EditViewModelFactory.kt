package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.melancholicbastard.handyasr.data.editor.AndroidDeleteFromCache
import com.melancholicbastard.handyasr.data.editor.AndroidReplaceFromCache
import com.melancholicbastard.handyasr.domain.editor.DeleteFromCacheUseCase
import com.melancholicbastard.handyasr.domain.editor.ReplaceFromCacheUseCase
import com.melancholicbastard.handyasr.presentation.App

class EditViewModelFactory(
    private val isNewRecord: Boolean,
    private val entity: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appContext = App.instance.applicationContext
        val androidReplaceFromCache = AndroidReplaceFromCache(appContext)
        val replaceFromCache = ReplaceFromCacheUseCase(androidReplaceFromCache)
        val androidDeleteFromCache = AndroidDeleteFromCache()
        val deleteFromCache = DeleteFromCacheUseCase(androidDeleteFromCache)
        return EditViewModel(
            isNewRecord = isNewRecord,
            entity = entity,
            replaceFromCache = replaceFromCache,
            deleteFromCache = deleteFromCache
        ) as T
    }
}