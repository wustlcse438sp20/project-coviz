package com.example.project_coviz.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocDao {

    @Query("SELECT * FROM locations")
    fun getLocations(): LiveData<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(loc: LocationEntity)

    @Delete
    fun delete(loc: LocationEntity)

    @Query("SELECT * FROM locations WHERE timestamp > :laterThan")
    fun getLatestLocations(laterThan: Long): LiveData<List<LocationEntity>>

}