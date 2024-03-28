package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

interface IWeatherRepository {
    fun getCurrentWeatherStats(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ): Flow<CurrentWeather>

    fun getWeekWeatherStats(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ): Flow<FiveDayForecast>

    //LOCAL
    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    suspend fun addToFavorites(location: FavoriteLocation)
    suspend fun removeFromFavorites(location: FavoriteLocation)
    fun getAllAlerts(): Flow<List<Alert>>

    suspend fun addToAlerts(alert: Alert)
    suspend fun removeFromAlerts(alert: Alert)
}