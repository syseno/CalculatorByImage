package com.example.calculator_camera.ui

sealed class ViewState<out T> {
    object Loading: ViewState<Nothing>()

    data class Success<out T>(val data: T): ViewState<T>()

    data class Failure<out T>(val message: String): ViewState<T>()
}