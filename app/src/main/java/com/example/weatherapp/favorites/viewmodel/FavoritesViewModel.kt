package com.example.weatherapp.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FavoritesViewModel(val repo: WeatherRepository): ViewModel() {
    private var _stateFlow: MutableStateFlow<List<FavoriteLocation>> = MutableStateFlow(listOf())
    val stateFlow: StateFlow<List<FavoriteLocation>> = _stateFlow.asStateFlow()
    fun add(location: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.add(location)
        }
    }
    fun remove(location: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.remove(location)
        }
    }
    fun getAllFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavorites().collect {
                _stateFlow.value = it
            }
        }
    }
}