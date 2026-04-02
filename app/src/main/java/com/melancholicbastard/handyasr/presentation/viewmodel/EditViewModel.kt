package com.melancholicbastard.handyasr.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditViewModel(
	val isNewRecord: Boolean
) : ViewModel() {
	init {
	    Log.d("ddd", "$this")
	}

	private val _integer = MutableStateFlow(1000)
	val integer : StateFlow<Int> = _integer.asStateFlow()

	fun decr() { _integer.value -= 1 }

	override fun onCleared() {
	    Log.d("ddd", "onCleared $this")
	}
}

