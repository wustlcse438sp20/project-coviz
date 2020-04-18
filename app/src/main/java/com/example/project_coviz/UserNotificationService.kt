package com.example.project_coviz

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.db.LocRepository
import com.example.project_coviz.db.LocRoomDatabase
import com.example.project_coviz.s2.S2CellId
import java.util.*


class UserNotificationService: LifecycleService() {
    private lateinit var locationRepo: LocRepository
    private lateinit var notificationManager: NotificationManager
    val MINUS_TIME_HOURS = 72
    val CHANNEL_ID = "CoVIZ"
    val NOTIFICATION_ID = 42069



    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        locationRepo = LocRepository(LocRoomDatabase.getDatabase(this).locDao())
        ApiClient.APIRepository.locationAndTimestamps.observe(this, Observer {
            val infectionLocations = it;
            val calendar: Calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR, -1*MINUS_TIME_HOURS)
            val date: Date = calendar.getTime()
            val intent = Intent(this, MapsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            locationRepo.getLatestLocations(date) {
                val userLocations = it
                if (userLocations.isEmpty()) {
                    Log.d("DEBUG","No data from DB")
                } else {
                    for (infectionLocation in infectionLocations.data) {
                        for (userLocation in userLocations) {
                            if (S2CellId.fromToken(infectionLocation.cell_token).intersects(S2CellId.fromToken(userLocation.cell_token))){
                                if(userLocation.time > infectionLocation.timestamp && !userLocation.notified){
                                    // Notify user that in bad area
                                    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.disclosure_icon)
                                        .setContentTitle("ALERT! You may have come into contact with SARS-CoV-2!")
                                        .setContentText("You may have come into contact with SARS-CoV-2! Tap here to view locations around you that have recent infections.")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(false)
                                        .build()
                                    notificationManager.notify(NOTIFICATION_ID, notification)
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
}