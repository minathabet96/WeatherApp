package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.model.FavoriteLocation

@Database(entities = [FavoriteLocation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val weatherDAO: WeatherDao

    companion object {
        private var database: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (database == null)
                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "locations_db"
                ).build()
            return database!!
        }
    }
}

