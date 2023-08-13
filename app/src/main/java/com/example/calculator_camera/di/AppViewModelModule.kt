package com.example.calculator_camera.di

import com.example.calculator_camera.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appViewModelModule = module {
    viewModel { MainViewModel(get()) }
}