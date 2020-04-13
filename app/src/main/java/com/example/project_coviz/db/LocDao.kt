package com.example.project_coviz.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocDao {

    @Query("SELECT * FROM locations")
    fun getLocations(): LiveData<List<Loc>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(loc: Loc)

    @Delete
    fun delete(loc: Loc)

    @Query("SELECT * FROM locations WHERE timestamp > :laterThan")
    fun getLatestLocations(laterThan: Long): LiveData<List<Loc>>

}