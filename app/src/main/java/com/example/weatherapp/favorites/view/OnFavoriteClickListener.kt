package com.example.weatherapp.favorites.view

import com.example.weatherapp.model.FavoriteLocation

interface OnFavoriteClickListener {
    fun onDeleteLocationClick(location: FavoriteLocation)
    fun onFavoriteLocationClick(location: FavoriteLocation)
}