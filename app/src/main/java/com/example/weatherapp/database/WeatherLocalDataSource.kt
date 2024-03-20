package com.example.weatherapp.database

import android.content.Context
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(c: Context) {
    private val dao = AppDatabase.getInstance(c).weatherDAO

    suspend fun add(location: FavoriteLocation){
        dao.insert(location)
    }

    suspend fun remove(location: FavoriteLocation) {
        dao.delete(location)

    }
    fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return dao.getAllFavorites()
    }
}
