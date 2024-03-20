package com.example.weatherapp.model

import com.example.weatherapp.database.WeatherLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

class WeatherRepository private constructor(
    private var remote: WeatherRemoteDataSource,
    private var local: WeatherLocalDataSource
) {
    companion object{
        private var instance: WeatherRepository? = null
        fun getInstance(
            remote: WeatherRemoteDataSource,
            local: WeatherLocalDataSource
        ): WeatherRepository {
            return instance?: synchronized(this){
                val temp = WeatherRepository(remote, local)
                instance = temp
                temp
            }
        }
    }

    fun getCurrentWeatherStats(lat: String, lon: String, lang: String, units: String): Flow<CurrentWeather> {
       return remote.getCurrentWeatherStats(lat, lon, lang, units)
    }
    fun getWeekWeatherStats(lat: String, lon: String, lang: String, units: String): Flow<FiveDayForecast> {
       return remote.getWeekWeatherStats(lat, lon, lang, units)
    }
    fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return local.getAllFavorites()
    }
    suspend fun add(location: FavoriteLocation){
        local.add(location)
    }
    suspend fun remove(location: FavoriteLocation){
        local.remove(location)
    }
}


