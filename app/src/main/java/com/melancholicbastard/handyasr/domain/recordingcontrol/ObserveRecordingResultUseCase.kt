package com.melancholicbastard.handyasr.domain.recordingcontrol

import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ObserveRecordingResultUseCase @Inject constructor(
    val provider: RecordingResultProvider
) {
    operator fun invoke(): SharedFlow<String> = provider.filePath
}