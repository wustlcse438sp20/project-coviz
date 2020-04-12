package com.example.project_coviz

import android.location.Location
import java.util.concurrent.locks.ReentrantLock

object LatestLocation {
    private val lock: ReentrantLock = ReentrantLock()

    @Volatile
    private var latestLocation: Location? = null

    @Volatile
    private var latestCellToken: String? = null

    fun getLatestLocation(): Location? {
        synchronized(lock) {
            return latestLocation
        }
    }

    fun getLatestCellToken(): String? {
        synchronized(lock) {
            return latestCellToken
        }
    }

    fun setLatestLocationAndCellToken(loc: Location, token: String) {
        synchronized(lock) {
            latestCellToken = token
            latestLocation = loc
        }
    }


}