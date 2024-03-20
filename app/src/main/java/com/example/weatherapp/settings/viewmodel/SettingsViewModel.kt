package com.example.weatherapp.settings.viewmodel

import android.content.Context
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Utils.DataStoreUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(context: Context): ViewModel() {
    private val dataStore = DataStoreUtil(context)

    fun setTemp(unit: String){
        viewModelScope.launch {
            dataStore.setTempUnit(unit)
        }
    }
    fun getTempUnit(): SharedFlow<String> {
        return dataStore.getTemp()
        }
}