package com.example.weatherapp.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.example.weatherapp.R
import com.example.weatherapp.utils.ApiResponse
import com.example.weatherapp.utils.unixToDateTime
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.home.viewModel.HomeViewModel
import com.example.weatherapp.home.viewModel.HomeViewModelFactory
import com.example.weatherapp.model.Day
import com.example.weatherapp.model.Step
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.settings.viewmodel.SettingsViewModelFactory
import com.example.weatherapp.utils.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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


    private val REQUEST_LOCATION_CODE = 1111
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
            this,
            SettingsViewModelFactory(requireContext())
        )[SettingsViewModel::class.java]
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onStart()
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation {
                    var windSpeedUnit = "metric"
                    var tempUnit = "metric"
                    var lang = "en"

                    lifecycleScope.launch {
                        settingsViewModel.getWindSpeedUnit().collect {
                            windSpeedUnit = it
                        }
                    }

                    lifecycleScope.launch {
                        settingsViewModel.getLocale().collect { languageCode ->
                            lang = languageCode
                            println("LANGUAGE: $lang")
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
                                    binding.weatherState.text = response.data.weather[0].description
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
                                    binding.windTxt.text =
                                        if (tempUnit == "imperial" && windSpeedUnit != "imperial")
                                            (response.data.wind.speed * 0.447).toString().take(3)
                                        else if (tempUnit == "metric" && windSpeedUnit == "imperial")
                                            (response.data.wind.speed * 2.2369).toString().take(3)
                                        else if (tempUnit == "standard" && windSpeedUnit == "imperial")
                                            (response.data.wind.speed * 2.2369).toString().take(3)
                                        else
                                            response.data.wind.speed.toString().take(3)

                                    binding.cloudTxt.text = response.data.clouds.all.toString()
                                    binding.visibilityTxt.text = response.data.visibility.toString()
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
                                    println("it is now loading")
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
                                            stepAdapter = StepAdapter(requireContext(), tempUnit)
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
                                    dayAdapter = DayAdapter(requireContext(), tempUnit)
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
                                    binding.failureLottie.visibility = VISIBLE
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
    fun getFreshLocation(callback: (List<String>) -> Unit) {
        var lat = ""
        var lon = ""
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                    getTextLocation(location!!.latitude, location.longitude)
                    lat = location.latitude.toString()
                    lon = location.longitude.toString()
                    callback(listOf(lat, lon))
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            },
            Looper.myLooper()
        )
    }

    fun getTextLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        binding.location.text = "Location"
        geocoder.getFromLocation(latitude, longitude, 1)
        { list -> binding.location.text = list[0].adminArea }

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
//    private fun setLocale(languageCode: String) {
//        val locale = Locale(languageCode)
//        Locale.setDefault(locale)
//        val config = Configuration()
//        config.locale = locale
//        resources.updateConfiguration(config, requireContext().resources.displayMetrics)
//
//        }



