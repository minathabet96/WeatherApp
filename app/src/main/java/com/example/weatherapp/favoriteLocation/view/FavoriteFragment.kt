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
import com.example.weatherapp.utils.ApiResponse
import com.example.weatherapp.utils.unixToDateTime
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentFavoriteBinding
import com.example.weatherapp.home.view.DayAdapter
import com.example.weatherapp.home.view.StepAdapter
import com.example.weatherapp.home.viewModel.HomeViewModel
import com.example.weatherapp.home.viewModel.HomeViewModelFactory
import com.example.weatherapp.model.Day
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.Step
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.settings.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FavoriteFragment : Fragment() {
    private lateinit var favoriteBinding: FragmentFavoriteBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var location: FavoriteLocation
    private lateinit var stepAdapter: StepAdapter
    private lateinit var dayAdapter: DayAdapter
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
        myLayoutManager = LinearLayoutManager(requireContext())
        homeViewModel = ViewModelProvider(
            this, HomeViewModelFactory(
                WeatherRepository
                    .getInstance(
                        WeatherRemoteDataSource(),
                        WeatherLocalDataSource(requireContext())
                    )
            )
        )[HomeViewModel::class.java]
        settingsViewModel = ViewModelProvider(
            this, SettingsViewModelFactory(requireContext()))[SettingsViewModel::class.java]

        favoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return favoriteBinding.root
    }

    override fun onStart() {
        super.onStart()
        var windSpeedUnit = "metric"
        var tempUnit = "metric"
        lifecycleScope.launch {
            settingsViewModel.getWindSpeedUnit().collect {
                windSpeedUnit = it
            }
        }
        lifecycleScope.launch {
            settingsViewModel.getTempUnit().collect { unit ->
                tempUnit = unit
                homeViewModel.getWeatherForToday(
                    location.latitude.toString(),
                    location.longitude.toString(),
                    "en",
                    tempUnit
                )
                homeViewModel.getFiveDayForecast(
                    location.latitude.toString(),
                    location.longitude.toString(),
                    "en",
                    tempUnit
                )
            }}
                getDate()
                lifecycleScope.launch {
                    homeViewModel.currentWeatherStateFlow.collect { response ->
                        when (response) {
                            is ApiResponse.Loading -> {

                            }

                            is ApiResponse.Success -> {
                                favoriteBinding.faveLocation.text = location.name
                                favoriteBinding.faveWeatherState.text =
                                    response.data.weather[0].description
                                favoriteBinding.faveDegree.text =
                                    response.data.main.temp.toInt().toString()
                                favoriteBinding.favePressureTxt.text =
                                    response.data.main.pressure.toString()
                                favoriteBinding.faveHumidityTxt.text =
                                    response.data.main.humidity.toString()
                                favoriteBinding.faveWindTxt.text =
                                    if(tempUnit == "imperial" && windSpeedUnit != "imperial")
                                        (response.data.wind.speed*0.447).toString()
                                    else if(tempUnit == "metric" && windSpeedUnit == "imperial")
                                        (response.data.wind.speed*2.2369).toString()
                                    else if(tempUnit == "standard" && windSpeedUnit == "imperial")
                                        (response.data.wind.speed*2.2369).toString()
                                    else
                                        response.data.wind.speed.toString()

                                favoriteBinding.windUnitTxt.text =
                                    when (tempUnit) {
                                            "imperial"  -> "mph"
                                            else -> "m/s"
                                    }
                                favoriteBinding.faveUnit.text =
                                    when (tempUnit) {
                                        "standard" -> "ºK"
                                        "imperial"  -> "ºF"
                                        else -> "ºC"
                                    }

                                favoriteBinding.faveCloudTxt.text =
                                    response.data.clouds.all.toString()
                                println("VISIBILITY:" + response.data.visibility.toString())
                                favoriteBinding.faveVisibilityTxt.text =
                                    response.data.visibility.toString()
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
                                val list = mutableListOf<Step>()
                                for (i in 0..7) {
                                    val hour = formatDate(response.data.list[i].dt)
                                    val temp =
                                        response.data.list[i].main.temp.toInt().toString()
                                    val icon = response.data.list[i].weather[0].icon
                                    val step = Step(hour, icon, temp)
                                    list.add(step)
                                    favoriteBinding.faveStepRecycler.apply {
                                        stepAdapter = StepAdapter(requireContext(), tempUnit)
                                        stepAdapter.submitList(list)
                                        adapter = stepAdapter
                                        myLayoutManager.orientation = RecyclerView.HORIZONTAL
                                        layoutManager = myLayoutManager
                                    }
                                }
                                val today = LocalDateTime.now().toLocalDate()
                                val filteredSteps =
                                    response.data.list.filter { unixToDateTime(it.dt).toLocalDate() != today }
                                val numberOfLastDaySteps = filteredSteps.size % 8
                                var xNum = 0
                                val daysList = mutableListOf<Day>()
                                for (singleDay in 0 until 4) {
                                    var dayMin: Int
                                    var dayMax: Int
                                    val date = Instant.ofEpochSecond(filteredSteps[xNum].dt)
                                        .atOffset(ZoneOffset.UTC)
                                        .toLocalDate()
                                    val dayOfWeek = date.dayOfWeek.getDisplayName(
                                        TextStyle.SHORT,
                                        Locale.ENGLISH
                                    ).take(3).capitalize()
                                    val weatherState =
                                        filteredSteps[xNum + 2].weather[0].description
                                    val icon = filteredSteps[xNum + 2].weather[0].icon
                                    val dayStepsList =
                                        mutableListOf<com.example.weatherapp.model.List>()

                                    for (i in xNum until xNum + 8)
                                        dayStepsList.add(filteredSteps[i])

                                    dayMin = dayStepsList[0].main.temp.toInt()
                                    dayMax = dayStepsList[0].main.temp.toInt()
                                    for (step in dayStepsList) {
                                        if (step.main.temp.toInt() < dayMin)
                                            dayMin = step.main.temp.toInt()
                                        if (step.main.temp.toInt() > dayMax)
                                            dayMax = step.main.temp.toInt()
                                    }
                                    daysList.add(
                                        Day(dayOfWeek, icon, weatherState, dayMax, dayMin)
                                    )
                                    xNum += 8
                                }
                                if (numberOfLastDaySteps != 0) {
                                    var dayMin: Int
                                    var dayMax: Int
                                    val date = Instant.ofEpochSecond(filteredSteps[xNum].dt)
                                        .atOffset(ZoneOffset.UTC)
                                        .toLocalDate()
                                    val dayOfWeek = date.dayOfWeek.getDisplayName(
                                        TextStyle.SHORT,
                                        Locale.ENGLISH
                                    ).take(3).capitalize()
                                    val weatherState =
                                        if (filteredSteps.size > 33)
                                            filteredSteps[xNum + 2].weather[0].description
                                        else
                                            filteredSteps[xNum].weather[0].description
                                    val icon =
                                        if (filteredSteps.size > 33)
                                            filteredSteps[34].weather[0].icon
                                        else
                                            filteredSteps[xNum].weather[0].icon

                                    val dayStepsList =
                                        mutableListOf<com.example.weatherapp.model.List>()

                                    for (i in xNum until filteredSteps.size)
                                        dayStepsList.add(filteredSteps[i])

                                    dayMin = dayStepsList[0].main.temp.toInt()
                                    dayMax = dayStepsList[0].main.temp.toInt()
                                    for (step in dayStepsList) {
                                        if (step.main.temp.toInt() < dayMin)
                                            dayMin = step.main.temp.toInt()
                                        if (step.main.temp.toInt() > dayMax)
                                            dayMax = step.main.temp.toInt()
                                    }
                                    daysList.add(
                                        Day(
                                            dayOfWeek,
                                            icon,
                                            weatherState,
                                            dayMax,
                                            dayMin
                                        )
                                    )
                                    xNum += 8
                                }
                                println(daysList)
                                dayAdapter = DayAdapter(requireContext(), tempUnit)
                                dayAdapter.submitList(daysList)
                                favoriteBinding.faveDayRecycler.apply {
                                    adapter = dayAdapter
                                    layoutManager = LinearLayoutManager(requireContext())
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