package com.melancholicbastard.handyasr.domain.recordingcontrol

interface RecordingCommandSender {
    fun send(command: RecordingCommand)
}

