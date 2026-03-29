package com.melancholicbastard.handyasr.data

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import com.melancholicbastard.handyasr.data.permission.AndroidMicrophonePermissionChecker
import com.melancholicbastard.handyasr.domain.AudioRecorderManager
import com.melancholicbastard.handyasr.domain.permission.MicrophonePermissionCheckUseCase
import com.melancholicbastard.handyasr.presentation.App
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

object AndroidAudioRecorderManager : AudioRecorderManager {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val microphonePermissionCheckUseCase = MicrophonePermissionCheckUseCase(
        AndroidMicrophonePermissionChecker(App.instance)
    )

    private var audioRecorder: AudioRecord? = null
    private var audioFile: File? = null
    private var audioRecordingJob: Job? = null
    @Volatile
    private var isPausedRecording: Boolean = false
    @Volatile
    private var isRecording: Boolean = false

    override suspend fun startAudioRecording() {
        if (!microphonePermissionCheckUseCase()) {
            Log.e("AudioRecManager", "Audio recording permission not granted")
            return
        }
        try {
            val context = App.instance
            val tmp = withContext(Dispatchers.IO) {
                File.createTempFile("handyasr_record_", ".wav", context.cacheDir)
            }
            audioFile = tmp

            val sampleRate = 16000
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = if (minBufSize == AudioRecord.ERROR || minBufSize == AudioRecord.ERROR_BAD_VALUE) {
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
                Log.e("AudioRecManager", "missing RECORD_AUDIO permission or start failed", sec)
                audioRecorder = null
                return
            }

            isRecording = true
            isPausedRecording = false

            audioRecordingJob = createAndRunAudioRecordingJob(bufferSize, sampleRate)

            Log.d("AudioRecManager", "started -> ${audioFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e("AudioRecManager", "start error", e)
        }
    }

    override fun pauseAudioRecording() {
        isPausedRecording = true
        Log.d("AudioRecManager", "paused")
    }

    override fun resumeAudioRecording() {
        isPausedRecording = false
        Log.d("AudioRecManager", "resumed")
    }

    override suspend fun stopAudioRecording(delete: Boolean): File? {
        try {
            isRecording = false

            try {
                audioRecorder?.stop()
                audioRecorder?.release()
            } catch (e: Exception) {
                Log.e("AudioRecManager", "stop() threw", e)
            }
            audioRecorder = null

            try {
                audioRecordingJob?.cancel()
                audioRecordingJob?.join()
            } catch (_: Throwable) {}
            audioRecordingJob = null

            val file = audioFile
            if (file == null) {
                Log.e("AudioRecManager", "no audio file to process")
                return null
            }

            if (delete) {
                try { if (file.exists()) file.delete() } catch (_: Throwable) {}
                audioFile = null
                return null
            }

            try {
                playFile(file)
            } catch (e: Exception) {
                Log.e("AudioRecManager", "play error", e)
            }
            return file
        } catch (e: Exception) {
            Log.e("AudioRecManager", "stop error", e)
        } finally {
            audioFile = null
        }
        return null
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
                    Log.e("AudioRecManager", "wav header finalize error", e)
                }
            }
        } catch (e: Exception) {
            Log.e("AudioRecManager", "recording job error", e)
        }
    }

    private suspend fun playFile(file: File) {
        withContext(Dispatchers.Main) {
            val mp = MediaPlayer()
            try {
                mp.setDataSource(file.absolutePath)
                mp.setOnPreparedListener { player -> player.start() }
                mp.setOnCompletionListener { player -> try { player.release() } catch (_: Throwable) {} }
                mp.setOnErrorListener { player, what, extra ->
                    try { player.release() } catch (_: Throwable) {}
                    Log.e("AudioRecManager", "MediaPlayer error what=$what extra=$extra")
                    true
                }
                mp.prepareAsync()
            } catch (e: Exception) {
                Log.e("AudioRecManager", "prepare/play failed", e)
                try { mp.release() } catch (_: Throwable) {}
            }
        }
    }

    private fun createWavHeader(totalAudioLen: Long, totalDataLen: Long, longSampleRate: Int, channels: Int, byteRate: Int): ByteArray {
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