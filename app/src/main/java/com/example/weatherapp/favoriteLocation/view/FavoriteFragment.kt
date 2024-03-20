package com.example.weatherapp.favoriteLocation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.Utils.ApiResponse
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentFavoriteBinding
import com.example.weatherapp.databinding.FragmentFavoritesBinding
import com.example.weatherapp.home.view.StepAdapter
import com.example.weatherapp.home.viewModel.HomeViewModel
import com.example.weatherapp.home.viewModel.HomeViewModelFactory
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.Step
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FavoriteFragment : Fragment() {
    private lateinit var favoriteBinding: FragmentFavoriteBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var location: FavoriteLocation
    private lateinit var stepAdapter: StepAdapter
    private lateinit var myLayoutManager: LinearLayoutManager
    private val args: FavoriteFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        location = args.location
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        stepAdapter = StepAdapter(requireContext())
        myLayoutManager = LinearLayoutManager(requireContext())
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(
            WeatherRepository
            .getInstance(WeatherRemoteDataSource(), WeatherLocalDataSource(requireContext())))
        )[HomeViewModel::class.java]
        favoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return favoriteBinding.root
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.getWeatherForToday(location.latitude.toString(), location.longitude.toString(), "en", "metric")
        homeViewModel.getFiveDayForecast(location.latitude.toString(), location.longitude.toString(), "en", "metric")
        getDate()
        lifecycleScope.launch {
            homeViewModel.currentWeatherStateFlow.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {

                    }

                    is ApiResponse.Success -> {
                        favoriteBinding.faveLocation.text = location.name
                        favoriteBinding.faveWeatherState.text = response.data.weather[0].description
                        favoriteBinding.faveDegree.text = response.data.main.temp.toInt().toString()
                        favoriteBinding.favePressureTxt.text =
                            response.data.main.pressure.toString()
                        favoriteBinding.faveHumidityTxt.text =
                            response.data.main.humidity.toString()
                        favoriteBinding.faveWindTxt.text = response.data.wind.speed.toString()
                        favoriteBinding.faveCloudTxt.text = response.data.clouds.all.toString()
                        println("VISIBILITY:" + response.data.visibility.toString())
                        favoriteBinding.faveVisibilityTxt.text = response.data.visibility.toString()
                    }

                    is ApiResponse.Failure -> {

                    }
                }
            }
        }
        lifecycleScope.launch {
            homeViewModel.fiveDayForecastStateFlow.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        favoriteBinding.faveProgressBar.visibility = View.VISIBLE
                        favoriteBinding.faveMainGroup.visibility = View.INVISIBLE
                    }

                    is ApiResponse.Success -> {
                        favoriteBinding.faveProgressBar.visibility = View.INVISIBLE
                        favoriteBinding.faveMainGroup.visibility = View.VISIBLE
                        println(response.data.list)
                        val list = mutableListOf<Step>()
                        for (i in 0..7) {
                            println(response.data.list[i])
                            val hour = formatDate(response.data.list[i].dt)
                            val temp =
                                response.data.list[i].main.temp.toInt().toString()
                            val step = Step(hour, "", temp)
                            list.add(step)
                            favoriteBinding.faveStepRecycler.apply {
                                stepAdapter.submitList(list)
                                adapter = stepAdapter
                                myLayoutManager.orientation = RecyclerView.HORIZONTAL
                                layoutManager = myLayoutManager
                            }
                        }

                    }

                    is ApiResponse.Failure -> {

                    }
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = timestamp * 1000 // Convert seconds to milliseconds
        }
        val dateFormat = SimpleDateFormat("h a", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return dateFormat.format(calendar.time)
    }
    private fun getDate() {
        val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.ENGLISH)
        favoriteBinding.faveDate.text = dateFormat.format(Date())
    }
}