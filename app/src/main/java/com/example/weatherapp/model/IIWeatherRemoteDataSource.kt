package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface IIWeatherRemoteDataSource {
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
}