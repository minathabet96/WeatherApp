package com.example.weatherapp.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SettingsViewModel::class.java))
            SettingsViewModel(context) as T
        else
            throw IllegalAccessException("class not found")
    }
}