package com.melancholicbastard.handyasr.domain.recordingcontrol

import kotlinx.coroutines.flow.SharedFlow

interface RecordingResultProvider {
    val filePath: SharedFlow<String>
}