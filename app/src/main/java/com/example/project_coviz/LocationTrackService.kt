package com.example.project_coviz

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ServiceCompat
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.db.Loc
import com.example.project_coviz.db.LocRepository
import com.example.project_coviz.db.LocRoomDatabase
import com.example.project_coviz.s2.S2CellId
import com.example.project_coviz.s2.S2LatLng
import com.google.android.gms.location.*

class LocationTrackService : Service() {
    val CELL_LEVEL = 16
    val REQUEST_INTERVAL_MS = 500000L
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRepo: LocRepository

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            val date = java.util.Date()
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    // ...
                    val cellId = S2CellId.fromLatLng(S2LatLng.fromDegrees(location.latitude, location.longitude)).parent(CELL_LEVEL).toToken()
                    val timestamp = date.getTime();
                    Log.d("LOCATION",
                            "Got location update! ${location.latitude}, ${location.longitude}")
                    Log.d("LOCATION", "Cell Id: ${cellId} Timestamp: ${timestamp}")

                    locationRepo.addLocation(Loc(cellId, timestamp)) { _ -> Unit}
                    LatestLocation.setLatestLocationAndCellToken(location, cellId)
                    ApiClient.APIRepository.updateTimestampsForCurrentLocation(cellId)
                }
            }
        }

        locationRepo = LocRepository(LocRoomDatabase.getDatabase(this).locDao())

        startLocationUpdates()
        Toast.makeText(this, "Tracking service started.", Toast.LENGTH_SHORT).show()

    }


    private fun startLocationUpdates() {
        val locationRequest =
            LocationRequest.create().setInterval(REQUEST_INTERVAL_MS).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
