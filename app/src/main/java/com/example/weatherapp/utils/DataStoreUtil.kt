package com.example.weatherapp.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.model.HomeLocation
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

class DataStoreUtil private constructor(context: Context) {

    private val Context.dataStore by preferencesDataStore("settings")
    private val dataStore = context.dataStore

    companion object {
        @Volatile
        private var instance: DataStoreUtil? = null

        fun getInstance(context: Context): DataStoreUtil {
            return instance ?: synchronized(this) {
                instance ?: DataStoreUtil(context.applicationContext).also { instance = it }
            }
        }
    }
        private val temp = stringPreferencesKey("temp")
        private val wind = stringPreferencesKey("wind")
        private val lang = stringPreferencesKey("lang")
        private val location = stringPreferencesKey("loc")
        private val homeLocation = stringPreferencesKey("homeLocation")

    suspend fun setTempUnit(unit: String) {
        dataStore.edit {
            it[temp] = unit
        }
    }

    fun getTemp(): SharedFlow<String> {
        return dataStore.data.map { it[temp] ?: "unknown"}.shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, 2)
//        return MutableSharedFlow<String>().apply {  dataStore.data
//            .map {
//                val unit = it[temp] ?: "metric"
//                emit(unit)
//            }
//            .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, 2)}
    }

    suspend fun setWindUnit(unit: String) {
        dataStore.edit {
            it[wind] = unit
        }
    }

    fun getWindUnit(): SharedFlow<String> {
        return dataStore.data
            .map {
                val unit = it[wind] ?: "metric"
                unit
            }
            .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), 1)
    }
    suspend fun setLang(languageCode: String) {
        dataStore.edit {
            it[lang] = languageCode
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
    suspend fun setLocationSetting(unit: String) {
        dataStore.edit {
            it[location] = unit
        }
    }

    fun getLocationSetting(): SharedFlow<String> {
        return dataStore.data
            .map {
                val unit = it[location] ?: "gps"
                unit
            }
            .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), 1)
    }


    suspend fun setHomeLocation(location: HomeLocation) {
        dataStore.edit {
            it[homeLocation] = Gson().toJson(location)
        }
    }

    fun getHomeLocation(): SharedFlow<String> {
        return dataStore.data
            .map {
                val unit = it[homeLocation] ?: ""
                unit
            }
            .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), 1)
    }
}

