package com.example.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//KEY: 8084bd2c7fd59779ef2676212ff216e8
object RetrofitHelper {
    const val  BASE_URL = "https://api.openweathermap.org/data/2.5/"
    val retrofitInstance = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
    val apiInstance: WeatherService = retrofitInstance.create(WeatherService::class.java)
}