package com.example.weatherapp.favorites.view

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.example.weatherapp.R
import com.example.weatherapp.alerts.view.showDialog
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentFavoritesBinding
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.UUID

class FavoritesFragment : Fragment(), OnFavoriteClickListener {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var theView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this, FavoritesViewModelFactory(
                (WeatherRepository.getInstance(
                    WeatherRemoteDataSource(),
                    WeatherLocalDataSource(requireContext())
                ))
            )
        )[FavoritesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoritesAdapter = FavoritesAdapter(this)
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        theView = view
        viewModel.getAllFavorites()
        lifecycleScope.launch {
            viewModel.favoritesStateFlow.collect {
                binding.rv.apply {
                    if (it.isEmpty()) {
                        println("empty")
                        binding.noFavorites.visibility = View.VISIBLE
                        binding.rv.visibility = View.GONE
                    } else {
                        binding.noFavorites.visibility = View.GONE
                        binding.rv.visibility = View.VISIBLE
                    }
                    favoritesAdapter.submitList(it)
                    adapter = favoritesAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
                binding.addFab.setOnClickListener {
                    val connectivityManager =
                        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    if (connectivityManager.activeNetwork == null)
                        Snackbar.make(
                            requireView(),
                            R.string.no_connection,
                            Snackbar.LENGTH_LONG
                        ).show()
                    else {
                        val action =
                            FavoritesFragmentDirections.actionFavoritesFragmentToMapsFragment2(true, false)
                        findNavController(view).navigate(action)
                    }
                }
            }
        }
    }

    override fun onDeleteLocationClick(location: FavoriteLocation) {
        showDialog(
            requireContext(),
            getString(R.string.deleteLocation),
            getString(R.string.deleteFavoriteDialogMessage),
            getString(R.string.delete),
            getString(R.string.cancel),
            { _, _ ->
                WorkManager.getInstance(requireContext())
                viewModel.removeFromFavorites(location)
            }) { dialog, _ -> dialog.dismiss() }
    }

    override fun onFavoriteLocationClick(location: FavoriteLocation) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToFavoriteFragment(location)
        findNavController(theView).navigate(action)
    }

}