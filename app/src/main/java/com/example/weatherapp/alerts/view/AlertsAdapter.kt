package com.example.weatherapp.alerts.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.utils.AlertDiffUtil
import com.example.weatherapp.databinding.AlertItemBinding
import com.example.weatherapp.model.Alert

class AlertsAdapter(var listener: (Alert) -> Unit) : ListAdapter<Alert, AlertsAdapter.ViewHolder>(AlertDiffUtil()) {

    lateinit var binding: AlertItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        binding.alertName.text = current.name

        binding.deleteIcon.setOnClickListener{
            listener(current)

        }
    }
    class ViewHolder(var binding: AlertItemBinding): RecyclerView.ViewHolder(binding.root)
}