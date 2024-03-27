package com.example.weatherapp.database

import android.content.Context
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(c: Context) {
    private val dao = AppDatabase.getInstance(c).weatherDAO

    suspend fun addToFavorites(location: FavoriteLocation){
        dao.insertIntoFavorites(location)
    }

    suspend fun removeFromFavorites(location: FavoriteLocation) {
        dao.deleteFromFavorites(location)

    }
    fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return dao.getAllFavorites()
    }
    suspend fun addToAlerts(alert: Alert){
        dao.insertIntoAlerts(alert)
    }

    suspend fun removeFromAlerts(alert: Alert) {
        dao.deleteFromAlerts(alert)

    }
    fun getAllAlerts(): Flow<List<Alert>> {
        return dao.getAllAlerts()
    }
}
