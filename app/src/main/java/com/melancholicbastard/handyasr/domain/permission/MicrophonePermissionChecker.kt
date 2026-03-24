package com.melancholicbastard.handyasr.domain.permission

interface MicrophonePermissionChecker {
    fun isMicrophonePermissionGranted(): Boolean
}