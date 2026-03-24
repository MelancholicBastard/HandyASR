package com.melancholicbastard.handyasr.domain.permission

abstract class CheckMicrophonePermissionUseCase(
    private val microphonePermissionChecker: MicrophonePermissionChecker
) {
    operator fun invoke(): Boolean = microphonePermissionChecker.isMicrophonePermissionGranted()
}
