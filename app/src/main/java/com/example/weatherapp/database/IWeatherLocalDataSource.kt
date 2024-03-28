package com.example.weatherapp.database

import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    suspend fun addToFavorites(location: FavoriteLocation)
    suspend fun removeFromFavorites(location: FavoriteLocation)
    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    suspend fun addToAlerts(alert: Alert)
    suspend fun removeFromAlerts(alert: Alert)
    fun getAllAlerts(): Flow<List<Alert>>
}