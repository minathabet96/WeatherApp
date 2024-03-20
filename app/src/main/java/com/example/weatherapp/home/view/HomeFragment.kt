package com.example.weatherapp.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.example.weatherapp.Utils.ApiResponse
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.home.viewModel.HomeViewModel
import com.example.weatherapp.home.viewModel.HomeViewModelFactory
import com.example.weatherapp.model.Step
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.settings.viewmodel.SettingsViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var stepAdapter: StepAdapter
    private lateinit var myLayoutManager: LinearLayoutManager


    val REQUEST_LOCATION_CODE = 1111
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        stepAdapter = StepAdapter(requireContext())
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

    override fun onStart() {
        super.onStart()
        println("onStart")
        if (checkPermissions()) {
            println("Permission granted")
            if (isLocationEnabled()) {
                println("Location Enabled")
                getFreshLocation {
                    lifecycleScope.launch {
                        checkTempUnit()
                        println("HAHA UNIT: ")
                        homeViewModel.getWeatherForToday(it[0], it[1], "en", "metric")
                        homeViewModel.getFiveDayForecast(it[0], it[1], "en", "metric")
                    }
                    lifecycleScope.launch {
                        homeViewModel.currentWeatherStateFlow.collect { response ->
                            when (response) {
                                is ApiResponse.Loading -> {

                                }

                                is ApiResponse.Success -> {
                                    binding.weatherState.text = response.data.weather[0].description
                                    binding.degree.text = response.data.main.temp.toInt().toString()
                                    binding.pressureTxt.text =
                                        response.data.main.pressure.toString()
                                    binding.humidityTxt.text =
                                        response.data.main.humidity.toString()
                                    binding.windTxt.text = response.data.wind.speed.toString()
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
                                    binding.progressBar.visibility = VISIBLE
                                    binding.mainGroup.visibility = INVISIBLE
                                }

                                is ApiResponse.Success -> {
                                    binding.progressBar.visibility = INVISIBLE
                                    binding.mainGroup.visibility = VISIBLE
                                    println(response.data.list)
                                    val list = mutableListOf<Step>()
                                    for (i in 0..7) {
                                        println(response.data.list[i])
                                        val hour = formatDate(response.data.list[i].dt)
                                        val temp =
                                            response.data.list[i].main.temp.toInt().toString()
                                        val step = Step(hour, "", temp)
                                        list.add(step)
                                        binding.stepRecycler.apply {
                                            stepAdapter.submitList(list)
                                            adapter = stepAdapter
                                            myLayoutManager.orientation = HORIZONTAL
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
            } else {
                enableLocationServices()
            }
        } else {
            println("not permitted")
            requestPermissions()
        }
        getDate()
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

    private fun getDate() {
        val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.ENGLISH)
        binding.date.text = dateFormat.format(Date())
    }

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = timestamp * 1000 // Convert seconds to milliseconds
        }

        val dateFormat = SimpleDateFormat("h a", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return dateFormat.format(calendar.time)
    }

    private suspend fun checkTempUnit() {
        println("we're inside")
        lifecycleScope.launch {
            var tempUnit: String
            settingsViewModel.getTempUnit().collect {
                println("it ->>>> $it")
                when (it) {
                    "celsius" -> {
                        tempUnit = "metric"
                    }

                    "kelvin" -> {
                        tempUnit = "standard"
                    }

                    "fehrenheit" -> {
                        tempUnit = "imperial"
                    }
                }
            }
        }
    }
}
