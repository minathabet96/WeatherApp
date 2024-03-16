package com.example.weatherapp.Utils

import com.example.weatherapp.model.CurrentWeather

sealed class ApiResponse<T> {
        data class Success<T>(val data: T): ApiResponse<T>()
        data class Failure<T>(val e: Throwable): ApiResponse<T>()
        class Loading<T>: ApiResponse<T>()

}

const val KEY = "8084bd2c7fd59779ef2676212ff216e8"