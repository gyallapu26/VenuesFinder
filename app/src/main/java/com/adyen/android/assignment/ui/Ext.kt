package com.adyen.android.assignment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.adyen.android.assignment.R
import pub.devrel.easypermissions.EasyPermissions


fun Context.hasLocationPermission() =
    EasyPermissions.hasPermissions(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

fun Activity.requestLocationPermission(requestCode: Int) {
    EasyPermissions.requestPermissions(
        this,
        this.getString(R.string.message_location_permission_rational),
        requestCode,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
}

fun View.visible() {
    if (!this.isVisible) this.visibility = View.VISIBLE
}

fun View.gone() {
    if (!this.isGone) this.visibility = View.GONE
}

/**
 * Check if there is any connectivity
 *
 * @param context
 * HelpContext to check if there is an internet connection.
 *
 * @return Connected to internet.
 */


@SuppressLint("MissingPermission")
fun Context?.isNetworkAvailable(): Boolean {
    if (this == null) return false
    val connectivityManager =
        this.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        try {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        } catch (e: Exception) {
        }
    }
    return false
}