package com.example.project_coviz.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "location")
data class LocationEntity (
    @ColumnInfo(name = "timestamp")
    val time: Long,
    @ColumnInfo(name = "datestamp")
    val date: Long,
    @ColumnInfo(name = "S2_cell_id")
    val s2: Long
)
{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}