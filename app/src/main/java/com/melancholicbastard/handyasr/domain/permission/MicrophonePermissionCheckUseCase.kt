package com.melancholicbastard.handyasr.domain.permission

class MicrophonePermissionCheckUseCase(
    private val microphonePermissionChecker: MicrophonePermissionChecker
) {
    operator fun invoke(): Boolean = microphonePermissionChecker.isMicrophonePermissionGranted()
}
