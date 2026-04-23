package com.melancholicbastard.handyasr.domain.decode

import java.io.File
import javax.inject.Inject

class DecodeAudioUseCase @Inject constructor(
	private val decodeRepository: DecodeRepository
) {
	suspend operator fun invoke(file: File): DecodeResult<String> {
		return decodeRepository.decodeAudio(file)
	}
}

