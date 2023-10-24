package com.khawi.network_base.model

sealed class AdvanceResult<out T> {
    data class Success<out T>(val data: T) : AdvanceResult<T>()
    data class Error(val fault: AdvanceError? = null) : AdvanceResult<Nothing>()
}
