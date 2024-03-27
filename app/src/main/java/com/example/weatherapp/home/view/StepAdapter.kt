package com.example.weatherapp.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.utils.StepDiffUtil
import com.example.weatherapp.databinding.StepItemBinding
import com.example.weatherapp.model.Step

class StepAdapter(private val context: Context, private val tempUnit: String) : ListAdapter<Step, StepAdapter.ViewHolder>(StepDiffUtil()) {

    lateinit var binding: StepItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = StepItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.hour.text = current.hour
        holder.binding.degree.text = current.temp
        Glide.with(context)
            .load("https://openweathermap.org/img/wn/${current.icon}@2x.png")
            .into(holder.binding.imageView)
        holder.binding.tempUnitTxt.text =
            when (tempUnit) {
                    "standard" -> getString(context, R.string.temp_unit_K)
                "imperial"  -> getString(context, R.string.temp_unit_F)
                else -> getString(context, R.string.temp_unit_C)
            }
    }
    class ViewHolder(var binding: StepItemBinding): RecyclerView.ViewHolder(binding.root)
}