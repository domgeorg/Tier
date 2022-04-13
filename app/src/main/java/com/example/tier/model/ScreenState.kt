package com.example.tier.model

sealed class ScreenState<out T> {
    data class Success<T>(val value: T) : ScreenState<T>()
    data class Error(val throwable: Throwable) : ScreenState<Nothing>()
    object Loading : ScreenState<Nothing>()
}
