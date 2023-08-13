package com.example.calculator.data

sealed class CalculatorResult {
    object Loading: CalculatorResult()

    data class Success(val result: String): CalculatorResult()

    data class Error(val message: String): CalculatorResult()
}
