package com.example.weatherapp.network

import com.example.weatherapp.model.Response
import retrofit2.http.GET
import retrofit2.http.Query



interface WeatherService {
    @GET("weather")
    suspend fun getCurrentWeatherStats(@Query("lat") lat: String, @Query("lon") lon: String, @Query("appid") key: String,
                                @Query("lang") lang: String, @Query("units") units: String, @Query("temp") tempUnit: String): retrofit2.Response<Response>
    @GET("onecall")
    suspend fun getWeekWeatherStats(@Query("lat") lat: String, @Query("lon") lon: String, @Query("appid") key: String,
                                @Query("lang") lang: String, @Query("units") units: String, @Query("temp") tempUnit: String): retrofit2.Response<Response>
}