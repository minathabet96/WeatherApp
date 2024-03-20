package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow
@Dao
interface WeatherDao {

    @Insert
    suspend fun insert(location: FavoriteLocation)

    @Delete
    suspend fun delete(location: FavoriteLocation)

    @Query("SELECT * FROM favoritelocation")
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
}