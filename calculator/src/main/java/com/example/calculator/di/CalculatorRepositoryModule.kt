package com.example.calculator.di

import com.example.calculator.data.repositories.CalculatorRepository
import com.example.calculator.data.repositories.CalculatorRepositoryImpl
import org.koin.dsl.module

val calculatorRepositoryModule = module {
    single { createRepository() }
}

fun createRepository(
) : CalculatorRepository = CalculatorRepositoryImpl()