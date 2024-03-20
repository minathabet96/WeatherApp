package com.example.weatherapp.favorites.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Utils.FavoritesDiffUtil
import com.example.weatherapp.databinding.FavoriteItemBinding
import com.example.weatherapp.model.FavoriteLocation

class FavoritesAdapter(private val onFavoriteClickListener: OnFavoriteClickListener): ListAdapter<FavoriteLocation, FavoritesAdapter.ViewHolder>(FavoritesDiffUtil()) {
    private lateinit var binding:FavoriteItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavoriteItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        binding.locationName.text = current.name
        binding.deleteIcon.setOnClickListener{
            onFavoriteClickListener.onDeleteLocationClick(current)
        }
        binding.constraintlayout.setOnClickListener{
            onFavoriteClickListener.onFavoriteLocationClick(current)
        }
    }
    class ViewHolder(binding: FavoriteItemBinding): RecyclerView.ViewHolder(binding.root){
    }
}