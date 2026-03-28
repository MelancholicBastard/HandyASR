package com.melancholicbastard.handyasr.domain

interface TimerManager {
    fun startTimer()
    fun pauseTimer()
    fun resumeTimer()
    fun stopTimer()
}