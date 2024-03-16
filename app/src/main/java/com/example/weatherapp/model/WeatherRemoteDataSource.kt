package com.example.weatherapp.model

import com.example.weatherapp.Utils.KEY
import com.example.weatherapp.network.RetrofitHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class WeatherRemoteDataSource {
    private val service = RetrofitHelper.apiInstance
    fun getCurrentWeatherStats(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ): Flow<CurrentWeather> {
        return flow {
            emit(
                service.getCurrentWeatherStats(lat, lon, KEY, lang, units).body()
                    ?: CurrentWeather()
            )
        }
    }

    fun getWeekWeatherStats(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ): Flow<FiveDayForecast>{
        return flow {
            emit(service.getWeekWeatherStats(lat, lon, KEY, lang, units).body()?: FiveDayForecast())
        }
    }
}