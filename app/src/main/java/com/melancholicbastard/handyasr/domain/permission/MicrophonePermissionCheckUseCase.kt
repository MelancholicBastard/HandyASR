package com.melancholicbastard.handyasr.domain.permission

import javax.inject.Inject

class MicrophonePermissionCheckUseCase @Inject constructor(
    private val microphonePermissionChecker: MicrophonePermissionChecker
) {
    operator fun invoke(): Boolean = microphonePermissionChecker.isMicrophonePermissionGranted()
}
