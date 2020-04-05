package com.example.project_coviz.db

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object RoomAccessor {
    private var db: AppDatabase? = null;

    fun getDatabase(context: Context): AppDatabase {
        if(db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "location-db"
            ).fallbackToDestructiveMigration().build()
        }
        return db!!;
    }

    fun <T, U> runFunction(f: (T) -> U, t: T) {
        CoroutineScope(Dispatchers.IO).launch {
            f(t)
        }
    }

    fun <U> runFunction(f: () -> U) {
        CoroutineScope(Dispatchers.IO).launch {
            f()
        }
    }

}