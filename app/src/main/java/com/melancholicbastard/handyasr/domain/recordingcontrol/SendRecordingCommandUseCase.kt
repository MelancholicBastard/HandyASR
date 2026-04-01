package com.melancholicbastard.handyasr.domain.recordingcontrol

class SendRecordingCommandUseCase(
    private val sender: RecordingCommandSender
) {
    operator fun invoke(command: RecordingCommand) {
        sender.send(command)
    }
}

