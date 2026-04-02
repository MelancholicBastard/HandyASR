package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel(

): ViewModel() {
    private val _integer = MutableStateFlow(0)
    val integer : StateFlow<Int> = _integer.asStateFlow()

    fun incr() { _integer.value += 1 }
}