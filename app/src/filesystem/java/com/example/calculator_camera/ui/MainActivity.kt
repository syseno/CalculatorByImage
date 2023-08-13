package com.example.calculator_camera.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.calculator_camera.databinding.ActivityMainBinding
import com.example.calculator_camera.utils.Permission
import com.example.calculator_camera.utils.PermissionManager
import com.example.common.base.activity.BaseActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.File

class MainActivity :
    BaseActivity<ActivityMainBinding, MainViewModel>(ActivityMainBinding::inflate) {

    private val permissionManager = PermissionManager.from(this)
    private val REQUEST_CODE_IMAGE = 201
    private var pickedBitmap: Bitmap? = null

    override fun setupViews() {
        viewModel.apply {
            observeResult()
        }
        setupButtonCaptureImage()
    }

    private fun setupButtonCaptureImage() {
        viewBinding.btnCaptureImage.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun computeFromImage(imageBitmap: Bitmap) {
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

        detector.processImage(firebaseVisionImage)
            .addOnSuccessListener { firebaseVisionText ->
                viewModel.doCalculate(firebaseVisionText.text)

            }
            .addOnFailureListener { e ->
                viewBinding.tvResult.text = e.message
            }

    }

    @SuppressLint("SetTextI18n")
    private fun checkCameraPermission() {
        permissionManager
            .request(Permission.Camera, Permission.Storage)
            .isShowRationale(true)
            .checkPermission {
                if (it.not()) {
                    viewBinding.tvResult.text = "You have to allow app to access Camera"
                } else {
                    pickImage()
                }
            }
    }

    private fun pickImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //No Permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            openCamera()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE && data != null) {
            val uri = data.data!!
            val inputStream = this.contentResolver.openInputStream(uri)
            val cursor = this.contentResolver.query(uri, null, null, null, null)
            cursor?.use { c ->
                val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (c.moveToFirst()) {
                    val name = c.getString(nameIndex)
                    inputStream?.let { inputStream ->
                        // create same file with same name
                        val file = File(this.cacheDir, name)
                        val os = file.outputStream()
                        os.use {
                            inputStream.copyTo(it)
                        }
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        viewBinding.imageView.setImageBitmap(bitmap)
                        bitmap?.let { computeFromImage(it) }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun MainViewModel.observeResult() {
        this.resultCalculateViewState.observe(this@MainActivity) { viewState ->
            when (viewState) {
                is ViewState.Loading -> {
                    viewBinding.tvResult.text = "Loading..."
                }

                is ViewState.Success -> {
                    viewBinding.tvResult.text = viewState.data
                }

                is ViewState.Failure -> {
                    viewBinding.tvResult.text = viewState.message
                }
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    override fun getInjectViewModel(): MainViewModel = getViewModel()
}