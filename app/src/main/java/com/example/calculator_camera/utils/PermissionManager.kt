package com.example.calculator_camera.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

@Suppress("TooManyFunctions")
class PermissionManager private constructor(private val activity: WeakReference<AppCompatActivity>) {

    private val requiredPermissions = mutableListOf<Permission>()
    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var isShowRationale: Boolean = false
    private var detailedCallback: (Map<Permission, Boolean>) -> Unit = {}

    private val permissionCheck =
        activity.get()
            ?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
                sendResultAndCleanUp(grantResults)
            }

    companion object {
        fun from(activity: AppCompatActivity) = PermissionManager(WeakReference(activity))
    }

    fun rationale(description: String): PermissionManager {
        rationale = description
        return this
    }

    fun isShowRationale(isShowRationale: Boolean): PermissionManager {
        this.isShowRationale = isShowRationale
        return this
    }

    fun request(vararg permission: Permission): PermissionManager {
        requiredPermissions.addAll(permission)
        return this
    }

    fun checkPermission(callback: (Boolean) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    private fun handlePermissionRequest() {
        activity.get()?.let { activity ->
            when {
                areAllPermissionsGranted(activity) -> sendPositiveResult()
                shouldShowPermissionRationale(activity) -> {
                    if (isShowRationale) {
                        displayRationale()
                    }
                }

                else -> requestPermissions()
            }
        }
    }

    private fun displayRationale() {
        requestPermissions()
    }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(getPermissionList().associateWith { true })
    }

    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        callback(grantResults.all { it.value })
        detailedCallback(grantResults.mapKeys { Permission.from(it.key) })
        cleanUp()
    }

    private fun cleanUp() {
        requiredPermissions.clear()
        rationale = null
        callback = {}
        detailedCallback = {}
    }

    private fun requestPermissions() {
        permissionCheck?.launch(getPermissionList())
    }

    private fun areAllPermissionsGranted(activity: AppCompatActivity) =
        requiredPermissions.all { it.isGranted(activity) }

    private fun shouldShowPermissionRationale(activity: AppCompatActivity) =
        requiredPermissions.any {
            it.requiresRationale(activity)
        }

    private fun getPermissionList() =
        requiredPermissions.flatMap { it.permissions.toList() }.toTypedArray()

    private fun Permission.isGranted(activity: AppCompatActivity) =
        permissions.all { hasPermission(activity, it) }

    private fun Permission.requiresRationale(activity: AppCompatActivity) =
        permissions.any { activity.shouldShowRequestPermissionRationale(it) }

    private fun hasPermission(fragment: Activity, permission: String) =
        ContextCompat.checkSelfPermission(
            fragment,
            permission
        ) == PackageManager.PERMISSION_GRANTED
}
