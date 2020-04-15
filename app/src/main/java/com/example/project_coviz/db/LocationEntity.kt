package com.example.project_coviz.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "locations")
data class LocationEntity (
    @ColumnInfo(name = "timestamp")
    val time: Long,
    @ColumnInfo(name = "S2_cell_id")
    val cell_token: String,
    @ColumnInfo(name = "notified", defaultValue = "false")
    var notified: Boolean
)
{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}