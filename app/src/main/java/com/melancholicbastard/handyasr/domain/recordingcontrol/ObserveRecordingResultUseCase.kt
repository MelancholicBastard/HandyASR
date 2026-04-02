package com.melancholicbastard.handyasr.domain.recordingcontrol

import kotlinx.coroutines.flow.SharedFlow

class ObserveRecordingResultUseCase(
    val provider: RecordingResultProvider
) {
    operator fun invoke(): SharedFlow<String> = provider.filePath
}