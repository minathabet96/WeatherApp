package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteDataSource: IIWeatherRemoteDataSource {
    override fun getCurrentWeatherStats(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ): Flow<CurrentWeather> {
        return  flowOf(
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
}