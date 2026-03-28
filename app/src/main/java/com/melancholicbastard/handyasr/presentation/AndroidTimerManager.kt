package com.melancholicbastard.handyasr.presentation

import com.melancholicbastard.handyasr.domain.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object AndroidTimerManager : TimerManager {
    private val _elapsedMs = MutableStateFlow(0L)
    val elapsedMs: StateFlow<Long> = _elapsedMs.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var tickerJob: Job? = null
    private var startTimeMs: Long = 0L
    private var accumulatedPauseMs: Long = 0L

    override fun startTimer() {
        startTimeMs = System.currentTimeMillis()
        accumulatedPauseMs = 0L
        _elapsedMs.value = 0L
        createJob()
    }

    override fun pauseTimer() {
        if (tickerJob?.isActive == true) {
            cancelJob()
        }
    }

    override fun resumeTimer() {
        startTimeMs = System.currentTimeMillis()
        accumulatedPauseMs = _elapsedMs.value
        createJob()
    }

    override fun stopTimer() {
        cancelJob()
        startTimeMs = 0L
        accumulatedPauseMs = 0L
        scope.launch { _elapsedMs.emit(0L) }
    }

    private fun createJob() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val elapsed = accumulatedPauseMs + now - startTimeMs
                _elapsedMs.value = if (elapsed >= 0L) elapsed else 0L
                delay(16L)
            }
        }
    }

    private fun cancelJob() {
        tickerJob?.cancel()
        tickerJob = null
    }
}