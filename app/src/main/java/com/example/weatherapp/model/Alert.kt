package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Alert(@PrimaryKey var name: String, val lat: Double, val lon: Double, var id: String, var time: Long): Serializable