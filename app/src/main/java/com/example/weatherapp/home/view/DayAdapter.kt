package com.example.weatherapp.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.utils.DayDiffUtil
import com.example.weatherapp.databinding.DayItemBinding
import com.example.weatherapp.model.Day

class DayAdapter(
    private val context: Context,
    private val tempUnit: String,
    private val recyclerView: RecyclerView,
    val listener: (Day) -> Unit
) : ListAdapter<Day, DayAdapter.ViewHolder>(DayDiffUtil()) {

    lateinit var binding: DayItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DayItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.day.text = current.dayName
        holder.binding.dayWeatherStatus.text = current.weatherState
        holder.binding.dayMaxTemp.text = current.max.toString()
        holder.binding.dayMinTemp.text = current.min.toString()
        Glide.with(context)
            .load("https://openweathermap.org/img/wn/${current.icon}@2x.png")
            .into(holder.binding.dayIcon)
        holder.binding.dayTempUnit.text =
            when (tempUnit) {
                "standard" -> ContextCompat.getString(context, R.string.temp_unit_K)
                "imperial" -> ContextCompat.getString(context, R.string.temp_unit_F)
                else -> ContextCompat.getString(context, R.string.temp_unit_C)
            }

        holder.binding.clayout.setOnClickListener {
            for (item in currentList) {
                if (current == item) {
                    holder.binding.clayout.setBackgroundResource(R.drawable.gradient)
                } else {
                    (recyclerView.findViewHolderForAdapterPosition(currentList.indexOf(item)) as ViewHolder).binding.clayout.setBackgroundResource(R.color.secondary)
                }
            }
            listener(current)
        }

    }

    class ViewHolder(val binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)
}