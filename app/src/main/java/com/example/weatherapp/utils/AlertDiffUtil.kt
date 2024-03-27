package com.example.weatherapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapp.model.Alert
class AlertDiffUtil: DiffUtil.ItemCallback<Alert>() {
    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return newItem == oldItem
    }
}