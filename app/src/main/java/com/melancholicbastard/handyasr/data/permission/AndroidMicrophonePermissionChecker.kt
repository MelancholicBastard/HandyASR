package com.melancholicbastard.handyasr.data.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidMicrophonePermissionChecker @Inject constructor(
    @param:ApplicationContext private val appContext: Context
) : MicrophonePermissionChecker {
    override fun isMicrophonePermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            appContext,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
}
