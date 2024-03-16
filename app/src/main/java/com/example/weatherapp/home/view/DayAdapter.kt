package com.example.weatherapp.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Utils.DayDiffUtil
import com.example.weatherapp.databinding.DayItemBinding
import com.example.weatherapp.model.Day

class DayAdapter(private val context: Context) : ListAdapter<Day, DayAdapter.ViewHolder>(DayDiffUtil()) {

    lateinit var binding: DayItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DayItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.day.text = current.dayName
        holder.binding.dayWeatherStatus.text = current.weatherState
        holder.binding.dayMaxTemp.text = current.max.toString()
        holder.binding.dayMinTemp.text = current.min.toString()
        //Glide.with(context).load()
    }
    class ViewHolder(var binding: DayItemBinding): RecyclerView.ViewHolder(binding.root)
}