package com.example.project_coviz

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.db.LocRepository
import com.example.project_coviz.db.LocRoomDatabase
import com.example.project_coviz.db.LocationEntity
import com.example.project_coviz.s2.S2CellId
import com.example.project_coviz.s2.S2LatLng
import com.google.android.gms.location.*
import java.util.*


class UnifiedLocationTrackService : LifecycleService() {
    val CHANNEL_ID = "CoVIZ"
    val NOTIFICATION_ID = 42069
    val FOREGROUND_ID = 1337


    private lateinit var notificationManager: NotificationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRepo: LocRepository

    override fun onCreate() {
        super.onCreate()
        locationRepo = LocRepository(LocRoomDatabase.getDatabase(this).locDao())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startLocationUpdates()
        startNotifications()

        val settingsIntent = Intent(this, MapsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        settingsIntent.putExtra("fragmentToStart", "settingsFragment")

        val settingsPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, settingsIntent, 0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.disclosure_icon)
            .setContentTitle("CoVIZ")
            .setContentText("CoVIZ is now tracking your location. We will notify you if you come into contact with any places with COVID-19 cases.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(settingsPendingIntent)
            .setAutoCancel(false)
            .build()

        startForeground(FOREGROUND_ID, notification)
    }

    private fun startNotifications() {
        createNotificationChannel()
        locationRepo = LocRepository(LocRoomDatabase.getDatabase(this).locDao())
        ApiClient.APIRepository.locationAndTimestamps.observe(this, androidx.lifecycle.Observer {
            val infectionLocations = it;
            val calendar: Calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR, -1 * Settings.HOURS_OF_DATA)
            val date: Date = calendar.getTime()
            val intent = Intent(this, MapsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            locationRepo.getLatestLocations(date) {
                val userLocations = it
                var notified = false
                if (userLocations.isEmpty()) {
                    Log.d("DEBUG","No data from DB")
                } else {
                    for (infectionLocation in infectionLocations.data) {
                        for (userLocation in userLocations) {
                            if (S2CellId.fromToken(infectionLocation.cell_token).intersects(S2CellId.fromToken(userLocation.cell_token))) {
                                if(userLocation.time > infectionLocation.timestamp && !userLocation.notified){
                                    // Notify user that in bad area
                                    if(Settings.notify && !notified) {
                                        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                                            .setSmallIcon(R.drawable.disclosure_icon)
                                            .setContentTitle("ALERT! You may have come into contact with SARS-CoV-2!")
                                            .setContentText("You may have come into contact with SARS-CoV-2! Tap here to view locations around you that have recent infections.")
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(false)
                                            .build()
                                        notificationManager.notify(NOTIFICATION_ID, notification)
                                        notified = true
                                    }
                                    userLocation.notified = true
                                    locationRepo.updateLocation(userLocation,{Unit})
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CoVIZ"
            val descriptionText = "Notifications from CoVIZ Service"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startLocationUpdates() {
        locationCallback = object : LocationCallback() {
            val date = java.util.Date()
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    // ...
                    val cellId = S2CellId.fromLatLng(S2LatLng.fromDegrees(location.latitude, location.longitude)).parent(Settings.CELL_LEVEL).toToken()
                    val timestamp = date.getTime()
                    Log.d("LOCATION",
                        "Got location update! ${location.latitude}, ${location.longitude}")
                    Log.d("LOCATION", "Cell Id: ${cellId} Timestamp: ${timestamp}")

                    locationRepo.addLocation(LocationEntity(timestamp, cellId, false)) { _ -> Unit}
                    Log.d("INSERT", "Inserted location entity with timestamp ${timestamp} and cell ID ${cellId}")
                    LatestLocation.setLatestLocationAndCellToken(location, cellId)
                    ApiClient.APIRepository.updateTimestampsForCurrentLocation(cellId, Settings.HOURS_OF_DATA)
//                    locationRepo.printLocations()
                }
            }
        }

        val locationRequest =
            LocationRequest.create().setInterval(Settings.REQUEST_INTERVAL_MS).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private fun stopForegroundService() { // Stop foreground service and remove the notification.
        stopForeground(true)
        // Stop the foreground service.
        stopSelf()
    }
}