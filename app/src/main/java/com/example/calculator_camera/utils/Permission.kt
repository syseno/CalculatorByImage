package com.example.calculator_camera.utils

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Build
import androidx.annotation.RequiresApi

sealed class Permission(vararg val permissions: String) {

    object Camera : Permission(CAMERA)

    object Location : Permission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object StorageTiramisuAbove : Permission(READ_MEDIA_IMAGES)

    object Storage : Permission(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object Notification : Permission(POST_NOTIFICATIONS)

    companion object {
        fun from(permission: String) = when (permission) {
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION -> Location
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> Storage
            CAMERA -> Camera
            POST_NOTIFICATIONS -> Notification
            READ_MEDIA_IMAGES -> Storage
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}
