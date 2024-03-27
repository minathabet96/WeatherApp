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
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.utils.Communicator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class MapsFragment : Fragment() {
    private lateinit var binding: FragmentMapsBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var fragmentManager: FragmentManager
    private val args: MapsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            requireActivity(), FavoritesViewModelFactory(
                (WeatherRepository.getInstance(
                    WeatherRemoteDataSource(),
                    WeatherLocalDataSource(requireContext())
                ))
            )
        )[FavoritesViewModel::class.java]

        fragmentManager = childFragmentManager
        fragmentManager.commit {
            this.addToBackStack("maps")
        }
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

        supportMapFragment.getMapAsync { googleMaps ->
            googleMaps.setOnMapClickListener { position ->
                val markerOptions = MarkerOptions()
                markerOptions.position(position)
                markerOptions.title(position.latitude.toString() + " : " + position.longitude)
                if (args.isInFavorites) {
                    binding.addToFavoritesBtn.visibility = View.VISIBLE
                    binding.addToFavoritesBtn.setOnClickListener {
                        lifecycleScope.launch {
                            val location = getTextLocation(position.latitude, position.longitude)
                            val favoriteLocation = FavoriteLocation(
                                location,
                                position.latitude,
                                position.longitude
                            )
                            viewModel.addToFavorites(favoriteLocation)
                            println("location: ${favoriteLocation.name}")
                            delay(500)
                        }
                        findNavController().navigate(R.id.favoritesFragment)
                    }
                }
                else {
                    binding.addToAlertsBtn.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            lifecycleScope.launch {
                                val location =
                                    getTextLocation(position.latitude, position.longitude)
                                val alert = Alert(location, position.latitude, position.longitude, "", 0)
                                viewModel.alert = alert
                                println(viewModel)
                                delay(500)
                                findNavController().navigate(R.id.alertsFragment)
                            }
                        }
                    }
                }
                googleMaps.clear()
                googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10F))
                googleMaps.addMarker(markerOptions)
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.addToFavoritesBtn.visibility = View.GONE
        binding.addToAlertsBtn.visibility = View.GONE
    }

    private fun addHoursAndMinutesToTimestamp(timestamp: Long, hours: Int, minutes: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.add(Calendar.HOUR, hours)
        calendar.add(Calendar.MINUTE, minutes)
        return calendar.timeInMillis
    }

    private suspend fun getTextLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val loc = withContext(Dispatchers.IO) {
            geocoder.getFromLocation(latitude, longitude, 1)
        }
        val location: String? =
            if((loc?.size ?: -1) >= 0)
                loc?.get(0)?.adminArea
            else
                "unknown location"

        println("location is: $location")
        return location ?: "LOC"
    }
}