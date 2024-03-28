package com.example.weatherapp.model

import com.example.weatherapp.database.IWeatherLocalDataSource
import com.example.weatherapp.database.WeatherLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

class WeatherRepository private constructor(
    private var remote: IIWeatherRemoteDataSource,
    private var local: IWeatherLocalDataSource
) : IWeatherRepository {
    companion object{
        private var instance: WeatherRepository? = null
        fun getInstance(
            remote: IIWeatherRemoteDataSource,
            local: IWeatherLocalDataSource
        ): WeatherRepository {
            return instance?: synchronized(this){
                val temp = WeatherRepository(remote, local)
                instance = temp
                temp
            }
        }
    }

    override fun getCurrentWeatherStats(lat: String, lon: String, lang: String, units: String): Flow<CurrentWeather> {
       return remote.getCurrentWeatherStats(lat, lon, lang, units)
    }
    override fun getWeekWeatherStats(lat: String, lon: String, lang: String, units: String): Flow<FiveDayForecast> {
       return remote.getWeekWeatherStats(lat, lon, lang, units)
    }


    //LOCAL
    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return local.getAllFavorites()
    }
    override suspend fun addToFavorites(location: FavoriteLocation){
        local.addToFavorites(location)
    }
    override suspend fun removeFromFavorites(location: FavoriteLocation){
        local.removeFromFavorites(location)
    }
    override fun getAllAlerts(): Flow<List<Alert>> {
        return local.getAllAlerts()
    }
    override suspend fun addToAlerts(alert: Alert){
        local.addToAlerts(alert)
    }
    override suspend fun removeFromAlerts(alert: Alert){
        local.removeFromAlerts(alert)
    }
}


