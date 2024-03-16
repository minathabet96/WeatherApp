package com.example.weatherapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.home.viewModel.HomeViewModel
import com.example.weatherapp.network.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
//KEY: 8084bd2c7fd59779ef2676212ff216e8
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.menu_symbol)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navigationView, navController)
    }

//                    val date = Date(x!!.list[0].dt * 1000)
//                    val calendar =
//                        Calendar.getInstance().apply { time = date }
//                    val dayName = calendar.get(Calendar.DAY_OF_WEEK)
//                        //for (item in x.list)
//                        for(item in x.list)
//                            println(item.weather[0].main)
//                }

            override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == android.R.id.home) {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.closeDrawer(
                    GravityCompat.START
                ) else binding.drawerLayout.openDrawer(GravityCompat.START)
            }
            return super.onOptionsItemSelected(item)
        }
}


