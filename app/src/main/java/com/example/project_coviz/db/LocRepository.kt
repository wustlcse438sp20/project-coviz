package com.example.project_coviz.db

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocRepository(private val locDao: LocDao) {

    val allLocations: LiveData<List<Loc>> = locDao.getLocations()

    fun getLocations(callback: (LiveData<List<Loc>>) -> Any) {
        CoroutineScope(Dispatchers.IO).launch {
            callback(locDao.getLocations())
        }
    }

    fun getLatestLocations(laterThan: java.util.Date, callback: (LiveData<List<Loc>>) -> Any) {
        CoroutineScope(Dispatchers.IO).launch {
            callback(locDao.getLatestLocations(laterThan.time))
        }
    }

    fun addLocation(loc: Loc, callback: (Loc) -> Any) {
        CoroutineScope(Dispatchers.IO).launch {
            locDao.insert(loc)
            callback(loc)
        }
    }

}