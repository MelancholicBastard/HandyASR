package com.melancholicbastard.handyasr.domain.decode

import java.io.File

class DecodeAudioUseCase(
	private val decodeRepository: DecodeRepository
) {
	suspend operator fun invoke(file: File): DecodeResult<String> {
		return decodeRepository.decodeAudio(file)
	}
}

