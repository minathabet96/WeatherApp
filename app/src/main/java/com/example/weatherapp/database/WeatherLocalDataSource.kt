package com.example.weatherapp.database

import android.content.Context
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(c: Context) : IWeatherLocalDataSource {
    private val dao = AppDatabase.getInstance(c).weatherDAO

    override suspend fun addToFavorites(location: FavoriteLocation){
        dao.insertIntoFavorites(location)
    }

    override suspend fun removeFromFavorites(location: FavoriteLocation) {
        dao.deleteFromFavorites(location)

    }
    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return dao.getAllFavorites()
    }
    override suspend fun addToAlerts(alert: Alert){
        dao.insertIntoAlerts(alert)
    }

    override suspend fun removeFromAlerts(alert: Alert) {
        dao.deleteFromAlerts(alert)

    }
    override fun getAllAlerts(): Flow<List<Alert>> {
        return dao.getAllAlerts()
    }
}
