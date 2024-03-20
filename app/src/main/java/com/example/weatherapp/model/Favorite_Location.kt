package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class FavoriteLocation(val name: String, @PrimaryKey val latitude: Double, val longitude: Double): Serializable