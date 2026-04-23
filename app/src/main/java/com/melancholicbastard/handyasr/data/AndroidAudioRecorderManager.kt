package com.melancholicbastard.handyasr.data

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.melancholicbastard.handyasr.domain.AudioRecorderManager
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionCheckUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAudioRecorderManager @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val microphonePermissionChecker: MicrophonePermissionCheckUseCase
) : AudioRecorderManager {
    companion object {
        private const val TAG = "AudioRecManager"
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var audioRecorder: AudioRecord? = null
    private var audioFile: File? = null
    private var audioRecordingJob: Job? = null
    @Volatile
    private var isPausedRecording: Boolean = false
    @Volatile
    private var isRecording: Boolean = false

    override suspend fun startAudioRecording() {
        if (!microphonePermissionChecker()) {
            Log.e(TAG, "Audio recording permission not granted")
            return
        }
        try {
            withContext(Dispatchers.IO) {
                clearCacheRecordFiles(appContext)
            }

            val tmp = withContext(Dispatchers.IO) {
                File.createTempFile("handyasr_record_", ".wav", appContext.cacheDir)
            }
            audioFile = tmp

            val sampleRate = 16000
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize =
                if (minBufSize == AudioRecord.ERROR || minBufSize == AudioRecord.ERROR_BAD_VALUE) {
                    sampleRate * 2
                } else {
                    minBufSize * 2
                }

            try {
                audioRecorder = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSize
                )

                audioRecorder?.startRecording()
            } catch (sec: SecurityException) {
                Log.e(TAG, "missing RECORD_AUDIO permission or start failed", sec)
                audioRecorder = null
                return
            }

            isRecording = true
            isPausedRecording = false

            audioRecordingJob = createAndRunAudioRecordingJob(bufferSize, sampleRate)

            Log.d(TAG, "started -> ${audioFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "start error", e)
        }
    }

    override fun pauseAudioRecording() {
        isPausedRecording = true
        Log.d(TAG, "paused")
    }

    override fun resumeAudioRecording() {
        isPausedRecording = false
        Log.d(TAG, "resumed")
    }

    override suspend fun stopAudioRecording(delete: Boolean): String? {
        try {
            isRecording = false

            try {
                audioRecorder?.stop()
                audioRecorder?.release()
            } catch (e: Exception) {
                Log.e(TAG, "stop() threw", e)
            }
            audioRecorder = null

            audioRecordingJob?.cancel()
            audioRecordingJob?.join()
            audioRecordingJob = null

            if (audioFile == null) {
                Log.e(TAG, "no audio file to process")
                return null
            }

            if (!delete) {
                return audioFile!!.absolutePath
            } else {
                if (audioFile?.exists() == true) audioFile?.delete()
                audioFile = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "stop error", e)
        }
        return null
    }

    private fun clearCacheRecordFiles(context: Context) {
        try {
            val cacheDir = context.cacheDir ?: return
            val files = cacheDir.listFiles() ?: return
            for (f in files) {
                try {
                    if (f.name.startsWith("handyasr_record_")) {
                        f.delete()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "failed to delete cache file ${f.name}", e)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "clearCacheRecordFiles failed", e)
        }
    }

    private fun createAndRunAudioRecordingJob(bufferSize: Int, sampleRate: Int) = scope.launch {
        try {
            val file = audioFile ?: return@launch
            FileOutputStream(file).use { fos ->
                fos.write(ByteArray(44))

                val buffer = ByteArray(bufferSize)
                while (isActive && isRecording) {
                    val read = audioRecorder?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0 && !isPausedRecording) {
                        fos.write(buffer, 0, read)
                    }
                    if (read <= 0) delay(5L)
                }

                try {
                    val totalAudioLen = file.length() - 44
                    val totalDataLen = totalAudioLen + 36
                    val channels = 1
                    val byteRate = 16 * sampleRate * channels / 8
                    val header = createWavHeader(totalAudioLen, totalDataLen, sampleRate, channels, byteRate)

                    RandomAccessFile(file, "rw").use { raf ->
                        raf.seek(0)
                        raf.write(header)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "wav header finalize error", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "recording job error", e)
        }
    }

    private fun createWavHeader(
        totalAudioLen: Long,
        totalDataLen: Long,
        longSampleRate: Int,
        channels: Int,
        byteRate: Int
    ): ByteArray {
        val header = ByteArray(44)

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        writeInt(header, 4, (totalDataLen + 8).toInt())
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        writeInt(header, 16, 16)
        writeShort(header, 20, 1.toShort())
        writeShort(header, 22, channels.toShort())
        writeInt(header, 24, longSampleRate)
        writeInt(header, 28, byteRate)
        writeShort(header, 32, (channels * 16 / 8).toShort())
        writeShort(header, 34, 16.toShort())
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        writeInt(header, 40, totalAudioLen.toInt())

        return header
    }

    private fun writeInt(header: ByteArray, offset: Int, value: Int) {
        header[offset] = (value and 0xff).toByte()
        header[offset + 1] = (value shr 8 and 0xff).toByte()
        header[offset + 2] = (value shr 16 and 0xff).toByte()
        header[offset + 3] = (value shr 24 and 0xff).toByte()
    }

    private fun writeShort(header: ByteArray, offset: Int, value: Short) {
        header[offset] = (value.toInt() and 0xff).toByte()
        header[offset + 1] = (value.toInt() shr 8 and 0xff).toByte()
    }
}