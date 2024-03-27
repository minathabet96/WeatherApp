package com.example.weatherapp.utils

import java.time.LocalDateTime
import java.time.ZoneOffset

sealed class ApiResponse<T> {
        data class Success<T>(val data: T): ApiResponse<T>()
        data class Failure<T>(val e: Throwable): ApiResponse<T>()
        class Loading<T>: ApiResponse<T>()

}

const val KEY = "8084bd2c7fd59779ef2676212ff216e8"
const val PART = "minutely,hourly,daily"
fun unixToDateTime(unixTime: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(unixTime, 0, ZoneOffset.UTC)
}