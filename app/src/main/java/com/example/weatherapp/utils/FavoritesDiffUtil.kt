package com.example.weatherapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapp.model.FavoriteLocation

class FavoritesDiffUtil: DiffUtil.ItemCallback<FavoriteLocation>() {
    override fun areItemsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {
        return oldItem.latitude == newItem.latitude
    }

    override fun areContentsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {
        return oldItem == newItem
    }
}