package com.example.weatherapp.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.IWeatherRepository
import com.example.weatherapp.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FavoritesViewModel(private val repo: IWeatherRepository): ViewModel() {

    var alert = Alert("", 0.0, 0.0, "", 0)

    private var _favoritesStateFlow: MutableStateFlow<List<FavoriteLocation>> = MutableStateFlow(listOf())
    val favoritesStateFlow: StateFlow<List<FavoriteLocation>> = _favoritesStateFlow.asStateFlow()

    private var _alertsStateFlow: MutableStateFlow<List<Alert>> = MutableStateFlow(listOf())
    val alertsStateFlow: StateFlow<List<Alert>> = _alertsStateFlow.asStateFlow()
    fun addToFavorites(location: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addToFavorites(location)
        }
    }
    fun removeFromFavorites(location: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeFromFavorites(location)
        }
    }
    fun getAllFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavorites().collect {
                _favoritesStateFlow.value = it
            }
        }
    }
    fun addToAlerts(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addToAlerts(alert)
        }
    }
    fun removeFromAlerts(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeFromAlerts(alert)
        }
    }
    fun getAllAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllAlerts().collect {
                _alertsStateFlow.value = it
            }
        }
    }
}