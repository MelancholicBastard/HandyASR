package com.melancholicbastard.handyasr.domain.recordingcontrol

import javax.inject.Inject

class SendRecordingCommandUseCase @Inject constructor(
    private val sender: RecordingCommandSender
) {
    operator fun invoke(command: RecordingCommand) {
        sender.send(command)
    }
}

