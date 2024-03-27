package com.example.weatherapp.network

import com.example.weatherapp.model.AlertResponse
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.FiveDayForecast
import com.example.weatherapp.model.HourlyWeather
import retrofit2.http.GET
import retrofit2.http.Query



interface WeatherService {
    @GET("weather")
    suspend fun getCurrentWeatherStats(@Query("lat") lat: String, @Query("lon") lon: String, @Query("appid") key: String,
                                @Query("lang") lang: String, @Query("units") units: String): retrofit2.Response<CurrentWeather>
    @GET("forecast")
    suspend fun getWeekWeatherStats(@Query("lat") lat: String, @Query("lon") lon: String, @Query("appid") key: String,
                                    @Query("lang") lang: String, @Query("units") units: String): retrofit2.Response<FiveDayForecast>
    @GET("onecall")
    suspend fun getAlert(@Query("lat") lat: String, @Query("lon") lon: String,
                         @Query("appid") key: String): retrofit2.Response<AlertResponse>
}