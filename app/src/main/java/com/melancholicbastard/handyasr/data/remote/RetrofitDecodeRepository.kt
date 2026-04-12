package com.melancholicbastard.handyasr.data.remote

import retrofit2.HttpException
import kotlinx.serialization.json.Json
import com.melancholicbastard.handyasr.data.dto.ApiErrorDto
import com.melancholicbastard.handyasr.domain.decode.DecodeRepository
import com.melancholicbastard.handyasr.domain.decode.DecodeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class RetrofitDecodeRepository(
    private val decodeApi: DecodeApi,
    private val jsonParser: Json
) : DecodeRepository {

    override suspend fun decodeAudio(file: File): DecodeResult<String> =
        withContext(Dispatchers.IO) {
            try {
                if (!file.exists()) {
                    return@withContext DecodeResult.Error("File not found: ${file.name}")
                }

                val requestBody = file.asRequestBody("audio/wav".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

                val responseDto = decodeApi.decode(filePart)

                DecodeResult.Success(responseDto.result.text)

            } catch (e: HttpException) {
                val errorMessage = parseHttpError(e)
                DecodeResult.Error(errorMessage)
            } catch (e: Exception) {
                DecodeResult.Error("${e.message}")
            }
        }

    private fun parseHttpError(exception: HttpException): String {
        return try {
            val errorBody = exception.response()?.errorBody()?.string()

            if (errorBody.isNullOrBlank()) {
                return "Сервер вернул ошибку ${exception.code()}"
            }

            val errorDto = jsonParser.decodeFromString<ApiErrorDto>(errorBody)

            errorDto.detail.ifBlank { "Сервер вернул ошибку ${exception.code()}" }

        } catch (e: Exception) {
            "Ошибка сервера ${exception.code()}: ${exception.message}"
        }
    }
}
