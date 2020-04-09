package com.example.project_coviz.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface LocDao {

    @Query("SELECT * FROM locations")
    fun getLocations(): LiveData<List<Loc>>

}