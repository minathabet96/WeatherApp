package com.example.weatherapp.Utils

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapp.model.Step

class StepDiffUtil: DiffUtil.ItemCallback<Step>() {
    override fun areItemsTheSame(oldItem: Step, newItem: Step): Boolean {
        return oldItem.hour == newItem.hour
    }

    override fun areContentsTheSame(oldItem: Step, newItem: Step): Boolean {
        return oldItem == newItem
    }
}