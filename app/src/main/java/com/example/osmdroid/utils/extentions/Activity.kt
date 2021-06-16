package com.example.osmdroid.utils.extentions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

val AppCompatActivity.isLocationPermissionGranted: Boolean
    get() {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

val AppCompatActivity.isGpsEnabled: Boolean
    get() {
        val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return try {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            false
        }
    }

fun AppCompatActivity.requestLocationPermission(requestCode: Int) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode
    )
}