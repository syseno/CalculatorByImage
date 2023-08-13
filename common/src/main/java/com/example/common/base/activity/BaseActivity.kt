package com.example.common.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding, VM : ViewModel>(val bindingFactory: (LayoutInflater) -> VB) :
    AppCompatActivity() {
    lateinit var viewBinding: VB
    lateinit var viewModel: VM

    abstract fun setupViews()
    abstract fun getInjectViewModel(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getInjectViewModel()
        viewBinding = bindingFactory(layoutInflater)
        setContentView(viewBinding.root)
        setupViews()
    }


}