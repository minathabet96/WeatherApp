package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow
@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntoFavorites(location: FavoriteLocation)

    @Delete
    suspend fun deleteFromFavorites(location: FavoriteLocation)

    @Query("SELECT * FROM favoritelocation")
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntoAlerts(alert: Alert)

    @Delete
    suspend fun deleteFromAlerts(alert: Alert)

    @Query("SELECT * FROM alert")
    fun getAllAlerts(): Flow<List<Alert>>
}