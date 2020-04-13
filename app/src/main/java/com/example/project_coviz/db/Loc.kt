package com.example.project_coviz.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="locations")
data class Loc(
    @ColumnInfo(name = "cellId") val cellId: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
) {
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0
}