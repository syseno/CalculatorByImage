package com.example.calculator_camera.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.data.CalculatorResult
import com.example.calculator.data.repositories.CalculatorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainViewModel(
    private val calculatorRepository: CalculatorRepository
) : ViewModel() {
    private val _resultCalculateViewState = MutableLiveData<ViewState<String>>()
    val resultCalculateViewState: LiveData<ViewState<String>> get() = _resultCalculateViewState

    fun doCalculate(text: String) {
        viewModelScope.launch(Dispatchers.Main) {
            calculatorRepository.doCalculate(text).collectLatest { calculatorResult ->
                when (calculatorResult) {
                    is CalculatorResult.Loading -> {
                        _resultCalculateViewState.postValue(ViewState.Loading)
                    }

                    is CalculatorResult.Success -> {
                        _resultCalculateViewState.postValue(ViewState.Success(calculatorResult.result))
                    }

                    is CalculatorResult.Error -> {
                        _resultCalculateViewState.postValue(ViewState.Failure(calculatorResult.message))
                    }
                }

            }
        }
    }
}