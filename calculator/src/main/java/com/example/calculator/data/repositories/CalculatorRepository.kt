package com.example.calculator.data.repositories

import com.example.calculator.data.CalculatorResult
import kotlinx.coroutines.flow.Flow

interface CalculatorRepository {
    fun doCalculate(text: String): Flow<CalculatorResult>
}