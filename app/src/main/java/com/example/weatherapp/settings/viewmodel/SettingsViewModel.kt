package com.example.weatherapp.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.HomeLocation
import com.example.weatherapp.utils.DataStoreUtil
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(context: Context) : ViewModel() {
    private val dataStore = DataStoreUtil.getInstance(context)

    fun setTempUnit(unit: String) {
        viewModelScope.launch {
            dataStore.setTempUnit(unit)
        }
    }

    fun getTempUnit(): SharedFlow<String> {
        return dataStore.getTemp()
    }
    fun setWindSpeedUnit(unit: String) {
        viewModelScope.launch {
            dataStore.setWindUnit(unit)
        }
    }

    fun getWindSpeedUnit(): SharedFlow<String> {
        return dataStore.getWindUnit()
    }

    fun setLocale(languageCode: String) {
        viewModelScope.launch {
            dataStore.setLang(languageCode)
        }
    }
    fun getTheLocale(callback: (String) -> Unit){
        viewModelScope.launch {
            getLocale().collect {
                callback(it)
            }
        }
    }
    fun getLocale(): SharedFlow<String> {
            return dataStore.getLang()
    }

    fun setLocation(location: String) {
        viewModelScope.launch {
            dataStore.setLocationSetting(location)
        }
    }

    fun getLocation(): SharedFlow<String> {
        return dataStore.getLocationSetting()
    }
    fun setHomeLocation(location: HomeLocation) {
        viewModelScope.launch {
            dataStore.setHomeLocation(location)
        }
    }

    fun getHomeLocation(): SharedFlow<String> {
        return dataStore.getHomeLocation()
    }

}