package com.example.calculator.data.repositories

import com.example.calculator.Calculator
import com.example.calculator.data.CalculatorResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CalculatorRepositoryImpl : CalculatorRepository {
    override fun doCalculate(text: String): Flow<CalculatorResult> {
        return flow {
            emit(CalculatorResult.Loading)
            try {
                val result = Calculator.compute(text)
                emit(CalculatorResult.Success(result.toString()))
            } catch (exception: NumberFormatException) {
                emit(CalculatorResult.Error(exception.message.orEmpty()))
            } catch (exception: IllegalArgumentException) {
                emit(CalculatorResult.Error(exception.message.orEmpty()))
            }
        }
    }
}