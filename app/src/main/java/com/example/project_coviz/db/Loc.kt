package com.example.project_coviz.db

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName="locations")
data class Loc(
    @ColumnInfo(name = "cellId") val cellId: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)