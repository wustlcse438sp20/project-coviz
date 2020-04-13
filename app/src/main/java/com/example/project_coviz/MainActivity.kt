package com.example.project_coviz

import android.Manifest
import android.Manifest.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

//Testing Jason
class MainActivity : AppCompatActivity() {

    fun checkPermissions(): Boolean {

        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessCoarseLocationApproved) {
            val backgroundLocationPermissionApproved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat
                    .checkSelfPermission(this, permission.ACCESS_BACKGROUND_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

            if (backgroundLocationPermissionApproved) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
                return true
            } else {
                // App can only access location in the foreground. Display a dialog
                // warning the user that your app must have all-the-time access to
                // location in order to function properly. Then, request background
                // location.
                AlertDialog.Builder(this)
                    .setTitle("Location Tracking")
                    .setMessage("In order to perform this app's intended function, it must be allowed to access your location even when not running. " +
                            "YOUR LOCATION WILL NOT BE SHARED WITHOUT YOUR CONSENT. Please consent to location updates before using the service.")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(
                        "I Consent",
                        null) // A null listener allows the button to dismiss the dialog and take no further action.
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(permission.ACCESS_BACKGROUND_LOCATION),
                        42
                    )
                }
//                checkPermissions()
                return false
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            AlertDialog.Builder(this)
                .setTitle("Location Tracking")
                .setMessage("In order to perform this app's intended function, it must be allowed to access your location even when not running. " +
                        "YOUR LOCATION WILL NOT BE SHARED WITHOUT YOUR CONSENT. Please consent to location updates before using the service.")
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    "I Consent",
                    null) // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()
                .show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(
                        permission.ACCESS_FINE_LOCATION,
                        permission.ACCESS_BACKGROUND_LOCATION),
                    42
                )
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(
                        permission.ACCESS_FINE_LOCATION),
                    42
                )
            }
//            checkPermissions()
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        while(!checkPermissions()) {
            //pass
        }
        startService(Intent(this, LocationTrackService::class.java))
        startActivity(Intent(this, MapsActivity::class.java))
    }
}
