package com.example.weatherapp.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    fun getLocale(): SharedFlow<String> {
            return dataStore.getLang()
    }

}