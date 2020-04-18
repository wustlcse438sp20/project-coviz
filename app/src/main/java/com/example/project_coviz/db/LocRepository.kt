package com.example.project_coviz.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocRepository(private val locDao: LocDao) {

    fun getLocations(callback: (List<LocationEntity>) -> Any) {
        CoroutineScope(Dispatchers.IO).launch {
            callback(locDao.getLocations())
        }
    }

    fun updateLocation(loc: LocationEntity, callback: (LocationEntity) -> Any) {
        CoroutineScope(Dispatchers.IO).launch {
            locDao.update(loc)
            callback(loc)
        }
    }

    fun getLatestLocations(laterThan: java.util.Date, callback: (List<LocationEntity>) -> Any) {
        CoroutineScope(Dispatchers.IO).launch {
            callback(locDao.getLatestLocations(laterThan.time))
        }
    }

    fun addLocation(loc: LocationEntity, callback: (LocationEntity) -> Any) {
        CoroutineScope(Dispatchers.IO).launch {
            locDao.insert(loc)
            callback(loc)
        }
    }

    fun printLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            for(loc in locDao.getLocations()) {
                println("WE GOT ONE: ${loc}")
            }
        }
    }

}