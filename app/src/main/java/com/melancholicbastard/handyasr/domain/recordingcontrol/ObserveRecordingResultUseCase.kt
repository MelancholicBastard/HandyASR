package com.melancholicbastard.handyasr.domain.recordingcontrol

import kotlinx.coroutines.flow.SharedFlow
import java.io.File

class ObserveRecordingResultUseCase(
    val provider: RecordingResultProvider
) {
    operator fun invoke(): SharedFlow<File> = provider.file
}