package com.example.weatherapp.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Utils.StepDiffUtil
import com.example.weatherapp.databinding.StepItemBinding
import com.example.weatherapp.model.Step

class StepAdapter(private val context: Context) : ListAdapter<Step, StepAdapter.ViewHolder>(StepDiffUtil()) {

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
            //Glide.with(context).load()
    }
    class ViewHolder(var binding: StepItemBinding): RecyclerView.ViewHolder(binding.root)
}