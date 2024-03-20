package com.example.weatherapp.Utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class DataStoreUtil(context: Context) {
    private val Context.dataStore by preferencesDataStore("settings")
    val dataStore = context.dataStore

    companion object {
        val temp = stringPreferencesKey("temp")
        val wind = stringPreferencesKey("wind")
        val lang = stringPreferencesKey("lang")
        val loc = stringPreferencesKey("loc")
    }

    suspend fun setTempUnit(unit: String) {
        dataStore.edit {
            it[temp] = unit
        }
    }

    fun getTemp(): MutableSharedFlow<String> {
        return MutableSharedFlow<String>().apply {  dataStore.data
            .map {
                val unit = it[temp] ?: "metric"
                emit(unit)
            }
            .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), 1)}
    }

    suspend fun setWindUnit(unit: String) {
        dataStore.edit {
            it[wind] = unit
        }
    }

    fun getWindUnit(): SharedFlow<String> {
        return dataStore.data
            .map {
                val unit = it[wind] ?: "ms"
                unit
            }
            .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), 1)
    }
    suspend fun setLang(unit: String) {
        dataStore.edit {
            it[lang] = unit
        }
    }

    fun getLang(): SharedFlow<String> {
        return dataStore.data
            .map {
                val unit = it[lang] ?: "en"
                unit
            }
            .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), 1)
    }
    suspend fun setLocation(unit: String) {
        dataStore.edit {
            it[loc] = unit
        }
    }

    fun getLocation(): SharedFlow<String> {
        return dataStore.data
            .map {
                val unit = it[loc] ?: "gps"
                unit
            }
            .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), 1)
    }
}

