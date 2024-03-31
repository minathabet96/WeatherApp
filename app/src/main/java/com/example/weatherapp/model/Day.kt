package com.example.weatherapp.model

import kotlin.collections.List

class Day(
    var dayName: String,
    val icon: String,
    val weatherState: String,
    val  max: Int,
    val min: Int,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val clouds: Clouds,
    val visibility: Int
)