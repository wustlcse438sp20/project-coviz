package com.example.project_coviz.db

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocRepository(private val locDao: LocDao) {

    val allLocations: LiveData<List<Loc>> = locDao.getLocations()

    fun getLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            locDao.getLocations()
        }
    }

}