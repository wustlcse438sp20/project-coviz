package com.example.project_coviz.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = arrayOf(LocationEntity::class),
    version = 2
)

public abstract class LocRoomDatabase: RoomDatabase() {

    abstract fun locDao(): LocDao

    companion object {

        @Volatile
        private var INSTANCE: LocRoomDatabase? = null

        fun getDatabase(context: Context): LocRoomDatabase {
            val temp = INSTANCE
            if (temp != null) {
                return temp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocRoomDatabase::class.java,
                    "location_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }

    }

}