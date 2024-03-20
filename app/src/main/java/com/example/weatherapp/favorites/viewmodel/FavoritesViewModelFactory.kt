package com.example.weatherapp.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.weatherapp.model.WeatherRepository
class FavoritesViewModelFactory(private val repo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoritesViewModel::class.java))
            FavoritesViewModel(repo) as T
        else
            throw IllegalAccessException("class not found")
    }
}