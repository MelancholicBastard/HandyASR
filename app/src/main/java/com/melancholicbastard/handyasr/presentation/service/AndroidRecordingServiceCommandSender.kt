package com.melancholicbastard.handyasr.presentation.service

import android.content.Intent
import androidx.core.content.ContextCompat
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingCommand
import com.melancholicbastard.handyasr.domain.recordingcontrol.RecordingCommandSender
import com.melancholicbastard.handyasr.presentation.App

class AndroidRecordingServiceCommandSender : RecordingCommandSender {
	override fun send(command: RecordingCommand) {
		val appContext = App.instance.applicationContext
		val action = when (command) {
			RecordingCommand.START -> RecordingService.ACTION_START
			RecordingCommand.PAUSE -> RecordingService.ACTION_PAUSE
			RecordingCommand.UNPAUSE -> RecordingService.ACTION_UNPAUSE
			RecordingCommand.ACCEPT -> RecordingService.ACTION_ACCEPT
			RecordingCommand.REJECT -> RecordingService.ACTION_REJECT
		}

		val intent = Intent(appContext, RecordingService::class.java).apply {
			this.action = action
		}
		ContextCompat.startForegroundService(appContext, intent)
	}
}

