package com.example.weatherapp.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.LocaleConfig
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.home.viewModel.HomeViewModel
import com.example.weatherapp.home.viewModel.HomeViewModelFactory
import com.example.weatherapp.model.Clouds
import com.example.weatherapp.model.Day
import com.example.weatherapp.model.HomeLocation
import com.example.weatherapp.model.Main
import com.example.weatherapp.model.Step
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.model.Wind
import com.example.weatherapp.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.settings.viewmodel.SettingsViewModelFactory
import com.example.weatherapp.utils.ApiResponse
import com.example.weatherapp.utils.unixToDateTime
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import kotlinx.coroutines.delay
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

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var stepAdapter: StepAdapter
    private lateinit var dayAdapter: DayAdapter
    private lateinit var myLayoutManager: LinearLayoutManager
    private lateinit var lang: String


    private val REQUEST_LOCATION_CODE = 1111
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        findNavController().popBackStack(R.id.homeFragment, false)
        homeViewModel = ViewModelProvider(
            this@HomeFragment, HomeViewModelFactory(
                WeatherRepository
                    .getInstance(
                        WeatherRemoteDataSource(),
                        WeatherLocalDataSource(requireContext())
                    )
            )
        )[HomeViewModel::class.java]
        settingsViewModel = ViewModelProvider(
            this@HomeFragment,
            SettingsViewModelFactory(requireContext())
        )[SettingsViewModel::class.java]
        myLayoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            settingsViewModel.getLocale().collect {
                lang = it
            }
        }

//                Locale.setDefault(Locale(it))
//                val config = Configuration()
//                config.locale = Locale(it)
//                resources.updateConfiguration(config, resources.displayMetrics)
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var location = "gps"
        var windSpeedUnit = "metric"
        var tempUnit = "metric"
        lifecycleScope.launch {
            settingsViewModel.getLocation().collect {
                location = it
            }

        }

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                lifecycleScope.launch {
                    delay(200)
                    getFreshLocation(location) {

                        lifecycleScope.launch {
                            settingsViewModel.getWindSpeedUnit().collect {
                                windSpeedUnit = it
                            }
                        }

                        lifecycleScope.launch {
                            settingsViewModel.getLocale().collect { languageCode ->
                                lang = languageCode
                                getTodaysDate(Locale(lang))
                                settingsViewModel.getTempUnit().collect { unit ->
                                    tempUnit = unit

                                    homeViewModel.getWeatherForToday(it[0], it[1], lang, unit)
                                    homeViewModel.getFiveDayForecast(it[0], it[1], lang, unit)
                                }
                            }
                        }

                        lifecycleScope.launch {
                            homeViewModel.currentWeatherStateFlow.collect { response ->
                                when (response) {
                                    is ApiResponse.Loading -> {

                                    }

                                    is ApiResponse.Success -> {
                                        binding.weatherState.text =
                                            response.data.weather[0].description
                                        binding.tempUnitTxt.text =
                                            when (tempUnit) {
                                                "standard" -> getString(R.string.temp_unit_K)
                                                "imperial" -> getString(R.string.temp_unit_F)
                                                else -> getString(R.string.temp_unit_C)
                                            }
                                        binding.windUnitTxt.text =
                                            when (windSpeedUnit) {
                                                "imperial" -> getString(R.string.milePerHour)
                                                else -> getString(R.string.meterPerSecond)
                                            }

                                        binding.degree.text =
                                            response.data.main.temp.toInt().toString()
                                        binding.pressureTxt.text =
                                            response.data.main.pressure.toString()
                                        binding.humidityTxt.text =
                                            response.data.main.humidity.toString()
                                        binding.cloudTxt.text =
                                            response.data.clouds.all.toString()
                                        binding.visibilityTxt.text =
                                            response.data.visibility.toString()
                                        binding.visibilityUnitTxt.text =
                                            getString(R.string.visibility_unit)
                                        response.data.visibility.toString()
                                        binding.pressureTxtUnit.text =
                                            getString(R.string.pressure_unit)
                                        Glide.with(requireContext())
                                            .load("https://openweathermap.org/img/wn/${response.data.weather[0].icon}@2x.png")
                                            .into(binding.currentWeatherIcon)
                                        binding.windTxt.text =
                                            if (tempUnit == "imperial" && windSpeedUnit != "imperial")
                                                (response.data.wind.speed * 0.447).toString()
                                                    .take(3)
                                            else if (tempUnit == "metric" && windSpeedUnit == "imperial")
                                                (response.data.wind.speed * 2.2369).toString()
                                                    .take(3)
                                            else if (tempUnit == "standard" && windSpeedUnit == "imperial")
                                                (response.data.wind.speed * 2.2369).toString()
                                                    .take(3)
                                            else
                                                response.data.wind.speed.toString().take(3)

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
                                        binding.failureGroup.visibility = GONE
                                        binding.mainGroup.visibility = GONE
                                        binding.loadingLottie.visibility = VISIBLE
                                    }

                                    is ApiResponse.Success -> {
                                        binding.failureGroup.visibility = GONE
                                        binding.loadingLottie.visibility = GONE
                                        binding.mainGroup.visibility = VISIBLE
                                        val list = mutableListOf<Step>()
                                        for (i in 0..7) {

                                            var hour =
                                                formatDate(response.data.list[i].dt, Locale(lang))
                                            val temp =
                                                response.data.list[i].main.temp.toInt().toString()
                                            val icon = response.data.list[i].weather[0].icon
                                            val step = Step(hour, icon, temp)
                                            list.add(step)
                                            binding.stepRecycler.apply {
                                                stepAdapter =
                                                    StepAdapter(requireContext(), tempUnit)
                                                stepAdapter.submitList(list)
                                                adapter = stepAdapter
                                                myLayoutManager.orientation = HORIZONTAL
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
                                            val dayWeather: List<Weather>
                                            val dayMain: Main
                                            val dayWind: Wind
                                            val dayClouds: Clouds
                                            val dayVisibility: Int
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
                                            dayWeather = dayStepsList[0].weather
                                            dayMain = dayStepsList[0].main
                                            dayWind = dayStepsList[0].wind
                                            dayClouds = dayStepsList[0].clouds
                                            dayVisibility = dayStepsList[0].visibility
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
                                                    dayMin,
                                                    dayWeather,
                                                    dayMain,
                                                    dayWind,
                                                    dayClouds,
                                                    dayVisibility
                                                )
                                            )
                                            xNum += 8
                                        }
                                        if (numberOfLastDaySteps != 0) {
                                            var dayMin: Int
                                            var dayMax: Int
                                            val dayWeather: List<Weather>
                                            val dayMain: Main
                                            val dayWind: Wind
                                            val dayClouds: Clouds
                                            val dayVisibility: Int
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
                                            dayWeather = dayStepsList[0].weather
                                            dayMain = dayStepsList[0].main
                                            dayWind = dayStepsList[0].wind
                                            dayClouds = dayStepsList[0].clouds
                                            dayVisibility = dayStepsList[0].visibility
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
                                                    dayMin,
                                                    dayWeather,
                                                    dayMain,
                                                    dayWind,
                                                    dayClouds,
                                                    dayVisibility
                                                )
                                            )
                                            xNum += 8
                                        }
                                        if (lang == "ar") {
                                            for (day in daysList) {
                                                when (day.dayName) {
                                                    "Sun" -> day.dayName = "الأحد"
                                                    "Mon" -> day.dayName = "الإثنين"
                                                    "Tue" -> day.dayName = "الثلاثاء"
                                                    "Wed" -> day.dayName = "الإربعاء"
                                                    "Thu" -> day.dayName = "الخميس"
                                                    "Fri" -> day.dayName = "الجمعة"
                                                    "Sat" -> day.dayName = "السبت"
                                                }
                                            }
                                        }
                                        dayAdapter = DayAdapter(
                                            requireContext(),
                                            tempUnit,
                                            binding.dayRecycler
                                        ) {
                                            binding.pressureTxt.text =
                                                it.main.pressure.toString()
                                            binding.humidityTxt.text =
                                                it.main.humidity.toString()
                                            binding.windTxt.text =
                                                if (tempUnit == "imperial" && windSpeedUnit != "imperial")
                                                    (it.wind.speed * 0.447).toString()
                                                        .take(3)
                                                else if (tempUnit == "metric" && windSpeedUnit == "imperial")
                                                    (it.wind.speed * 2.2369).toString()
                                                        .take(3)
                                                else if (tempUnit == "standard" && windSpeedUnit == "imperial")
                                                    (it.wind.speed * 2.2369).toString()
                                                        .take(3)
                                                else
                                                    it.wind.speed.toString().take(3)

                                            binding.cloudTxt.text = it.clouds.all.toString()
                                            binding.visibilityTxt.text =
                                                it.visibility.toString()
                                        }
                                        dayAdapter.submitList(daysList)
                                        binding.dayRecycler.apply {
                                            adapter = dayAdapter
                                            layoutManager = LinearLayoutManager(requireContext())
                                        }
                                    }

                                    is ApiResponse.Failure -> {
                                        println("it is now a failure")
                                        binding.loadingLottie.visibility = GONE
                                        binding.mainGroup.visibility = GONE
                                        binding.failureGroup.visibility = VISIBLE
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                enableLocationServices()
            }
        } else {
            println("not permitted")
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return PermissionChecker.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED
                || PermissionChecker.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_CODE
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun enableLocationServices() {
        Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_SHORT).show()
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation(location: String, callback: (List<String>) -> Unit) {
        var lat = ""
        var lon = ""
        println("and location: $location")
        if (location == "gps") {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.Builder(0).apply {
                    setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                }.build(),
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        val myLocation = locationResult.lastLocation
                        println("my location: ${myLocation?.latitude} && ${myLocation?.longitude}")
                        getTextLocation(myLocation!!.latitude, myLocation.longitude, Locale(lang)) {
                            binding.location.text = it
                            lat = myLocation.latitude.toString()
                            lon = myLocation.longitude.toString()
                            callback(listOf(lat, lon))
                            fusedLocationProviderClient.removeLocationUpdates(this)
                        }
                    }
                },
                Looper.myLooper()
            )
        } else {

            lifecycleScope.launch {
                settingsViewModel.getHomeLocation().collect {
                    val homeLocation = Gson().fromJson(it, HomeLocation::class.java)
                    getTextLocation(
                        homeLocation.lat.toDouble(),
                        homeLocation.lon.toDouble(),
                        Locale(lang)
                    ) { location -> binding.location.text = location }

                    callback(listOf(homeLocation.lat, homeLocation.lon, homeLocation.name))
                }
            }

        }
    }

    private fun getTextLocation(
        latitude: Double,
        longitude: Double,
        locale: Locale,
        locationCallback: (String) -> Unit
    ) {
        var location = "Unrecognized Area"
        val geocoder = Geocoder(requireContext(), locale)
        geocoder.getFromLocation(latitude, longitude, 1) {
            if (it.size > 0) {
                location = if (it[0]?.adminArea != null)
                    it[0].adminArea + ", " + it[0].countryName
                else
                    it[0]?.countryName.toString()
            }
            locationCallback(location)
        }

    }

    private fun getTodaysDate(locale: Locale) {
        val dateFormat = SimpleDateFormat("EEE, d MMM", locale)
        binding.date.text = dateFormat.format(Date())
    }

    private fun formatDate(timestamp: Long, locale: Locale): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = timestamp * 1000 // Convert seconds to milliseconds
        }
        val dateFormat = SimpleDateFormat("h a", locale)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return dateFormat.format(calendar.time)
    }
}
