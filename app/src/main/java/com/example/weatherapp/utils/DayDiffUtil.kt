package com.example.weatherapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapp.model.Day

class DayDiffUtil: DiffUtil.ItemCallback<Day>() {
    override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean {
        return oldItem.dayName == newItem.dayName
    }

    override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean {
        return newItem == oldItem
    }
}