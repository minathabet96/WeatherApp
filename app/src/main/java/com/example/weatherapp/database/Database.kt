package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteLocation

@Database(entities = [FavoriteLocation::class, Alert::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract val weatherDAO: WeatherDao

    companion object {
        private var database: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (database == null)
                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "locations_db"
                ).fallbackToDestructiveMigration().build()
            return database!!
        }
    }
}

