package com.melancholicbastard.handyasr.domain.decode

sealed interface DecodeResult<out T> {
    data class Success<out T>(val text: T) : DecodeResult<T>
    data class Error<out T>(val detail: T) : DecodeResult<T>

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
}