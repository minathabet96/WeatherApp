package com.example.weatherapp.model

import com.example.weatherapp.database.IWeatherLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.collections.List

class FakeLocalDataSource(
    private val faveLocations: MutableList<FavoriteLocation>? = mutableListOf(),
    private val alerts: MutableList<Alert>? = mutableListOf()
): IWeatherLocalDataSource {
    override suspend fun addToFavorites(location: FavoriteLocation) {
        faveLocations?.add(location)
    }

    override suspend fun removeFromFavorites(location: FavoriteLocation) {
        faveLocations?.remove(location)
    }

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return flowOf(
            faveLocations?: listOf()
        )
    }

    override suspend fun addToAlerts(alert: Alert) {
        alerts?.add(alert)
    }

    override suspend fun removeFromAlerts(alert: Alert) {
        alerts?.remove(alert)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return flowOf(
            alerts?: listOf()
        )
    }
}