package com.example.calculator_camera

import android.app.Application
import com.example.calculator.di.calculatorRepositoryModule
import com.example.calculator_camera.di.appViewModelModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    calculatorRepositoryModule,
                    appViewModelModule
                )
            )
        }
    }
}