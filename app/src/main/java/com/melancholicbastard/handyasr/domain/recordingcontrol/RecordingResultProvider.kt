package com.melancholicbastard.handyasr.domain.recordingcontrol

import kotlinx.coroutines.flow.SharedFlow
import java.io.File

interface RecordingResultProvider {
    val file: SharedFlow<File>
}