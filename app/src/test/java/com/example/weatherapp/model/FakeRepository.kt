package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlin.collections.List

class FakeRepository(
    private var locationsList: MutableList<FavoriteLocation>,
    private var alertsList: MutableList<Alert>
): IWeatherRepository {
    override fun getCurrentWeatherStats(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ): Flow<CurrentWeather> {
        return flowOf(
            CurrentWeather()
        )
    }

    override fun getWeekWeatherStats(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ): Flow<FiveDayForecast> {
        return flowOf(
            FiveDayForecast()
        )
    }

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return flowOf(locationsList)
    }

    override suspend fun addToFavorites(location: FavoriteLocation) {
        locationsList.add(location)
    }

    override suspend fun removeFromFavorites(location: FavoriteLocation) {
        locationsList.remove(location)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return flowOf(alertsList)
    }

    override suspend fun addToAlerts(alert: Alert) {
        alertsList.add(alert)
    }

    override suspend fun removeFromAlerts(alert: Alert) {
        alertsList.remove(alert)
    }
}