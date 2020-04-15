package com.example.project_coviz

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.db.LocRepository
import com.example.project_coviz.db.LocRoomDatabase
import java.util.*


class UserNotificationService: LifecycleService() {
    private lateinit var locationRepo: LocRepository
    val MINUS_TIME_HOURS = 72
    override fun onCreate() {
        super.onCreate()
        locationRepo = LocRepository(LocRoomDatabase.getDatabase(this).locDao())
        ApiClient.APIRepository.locationAndTimestamps.observe(this, Observer{
            val infectionLocations = it;
            val calendar: Calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR, -1*MINUS_TIME_HOURS)
            val date: Date = calendar.getTime()
            locationRepo.getLatestLocations(date) {
                val userLocations = it.value
                if (userLocations == null) {
                    Log.d("Error","Error getting user location for user notification check")
                } else {
                    for (infectionLocation in infectionLocations.data) {
                        for (userLocation in userLocations) {
                            if (userLocation.cell_token == infectionLocation.cell_token){
                                if(userLocation.time > infectionLocation.timestamp){
                                    // Notify user that in bad area
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
}