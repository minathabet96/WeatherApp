package com.example.weatherapp.model

import com.example.weatherapp.utils.KEY
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.utils.PART
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
    fun getAlert(
        lat: String,
        lon: String,
    ): Flow<AlertResponse>{
        return flow {
            //emit(service.getAlert(lat, lon, PART, KEY).body()?: AlertResponse(Current(0.0), Alerts("")))
        }
    }
}