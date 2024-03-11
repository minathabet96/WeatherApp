package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.network.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val retro = RetrofitHelper.apiInstance
        lifecycleScope.launch(Dispatchers.IO) {
            val response = retro.getWeekWeatherStats("30.0444", "31.2357", "8084bd2c7fd59779ef2676212ff216e8", "en", "metric", "Celsius")
            withContext(Dispatchers.Main){
                println("RESPONSE CODE: "+response.code())
                val x = response.body()
                println(response.body())
            }
        }
    }
}