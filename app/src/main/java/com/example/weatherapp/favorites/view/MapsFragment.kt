package com.example.weatherapp.favorites.view

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentMapsBinding
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.HomeLocation
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.settings.viewmodel.SettingsViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class MapsFragment : Fragment() {
    private lateinit var binding: FragmentMapsBinding
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private val args: MapsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        favoritesViewModel = ViewModelProvider(
            requireActivity(), FavoritesViewModelFactory(
                (WeatherRepository.getInstance(
                    WeatherRemoteDataSource(),
                    WeatherLocalDataSource(requireContext())
                ))
            )
        )[FavoritesViewModel::class.java]
        settingsViewModel = ViewModelProvider(
            requireActivity(), SettingsViewModelFactory(requireContext())
        )[SettingsViewModel::class.java]


        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

        supportMapFragment.getMapAsync { googleMaps ->
            googleMaps.setOnMapClickListener { position ->
                getTextLocation(position.latitude, position.longitude) { location ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        googleMaps.clear()
                        val markerOptions = MarkerOptions()
                        markerOptions.position(position)
                        markerOptions.title(location)
                        googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 5F))
                        googleMaps.addMarker(markerOptions)

                        if (args.isInSettings) {
                            binding.addToFavoritesBtn.text =
                                resources.getString(R.string.chooseLocation)
                            binding.addToFavoritesBtn.visibility = View.VISIBLE
                            binding.addToFavoritesBtn.setOnClickListener {
                                val homeLocation = HomeLocation(
                                    location,
                                    position.latitude.toString(),
                                    position.longitude.toString()
                                )
                                settingsViewModel.setHomeLocation(homeLocation)
                                findNavController().popBackStack(R.id.settingsFragment, true)
//                        findNavController().navigate(R.id.homeFragment)
                            }

                        } else {
                            if (args.isInFavorites) {
                                binding.addToFavoritesBtn.visibility = View.VISIBLE
                                binding.addToFavoritesBtn.setOnClickListener {
                                    lifecycleScope.launch {

                                        val favoriteLocation = FavoriteLocation(
                                            location,
                                            position.latitude,
                                            position.longitude
                                        )
                                        favoritesViewModel.addToFavorites(favoriteLocation)
                                        delay(100)
                                    }
                                    findNavController().navigate(R.id.favoritesFragment)
                                }
                            } else {
                                binding.addToAlertsBtn.apply {
                                    visibility = View.VISIBLE
                                    setOnClickListener {
                                        lifecycleScope.launch {
                                            val alert = Alert(
                                                location,
                                                position.latitude,
                                                position.longitude,
                                                "",
                                                0
                                            )
                                            favoritesViewModel.alert = alert
                                            delay(100)
                                            findNavController().navigate(R.id.alertsFragment)


                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.addToFavoritesBtn.visibility = View.GONE
        binding.addToAlertsBtn.visibility = View.GONE
    }


    private fun getTextLocation(latitude: Double, longitude: Double, callback: (String) -> Unit) {
        var location = "Unrecognized Area"
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        geocoder.getFromLocation(latitude, longitude, 1) {
            if (it.size > 0) {
                location = if (it[0]?.adminArea != null)
                    it[0].adminArea + ", " + it[0].countryName
                else
                    it[0]?.countryName.toString()
            }
            callback(location)
        }
    }

    private fun addHoursAndMinutesToTimestamp(timestamp: Long, hours: Int, minutes: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.add(Calendar.HOUR, hours)
        calendar.add(Calendar.MINUTE, minutes)
        return calendar.timeInMillis
    }
}