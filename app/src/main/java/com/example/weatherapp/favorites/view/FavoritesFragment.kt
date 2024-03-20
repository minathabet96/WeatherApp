package com.example.weatherapp.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentFavoritesBinding
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment(), OnFavoriteClickListener {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var theView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, FavoritesViewModelFactory(
            (WeatherRepository.getInstance(WeatherRemoteDataSource(), WeatherLocalDataSource(requireContext())))
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
            viewModel.stateFlow.collect {
                binding.rv.apply {
                    println("list size tho: ${it.size}")
                    favoritesAdapter.submitList(it)
                    adapter = favoritesAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
                binding.addFab.setOnClickListener{
                    Toast.makeText(requireContext(), "fab clicked", Toast.LENGTH_SHORT).show()
                    findNavController(view).navigate(R.id.mapsFragment)
                //                    navController.navigate(R.id.mapsFragment)
//                    val fragmentManager: FragmentManager = childFragmentManager
//                    fragmentManager.commit {
//                        this.replace(R.id.nav_host_fragment, MapsFragment(), null)
//                            .setReorderingAllowed(true)
//                            .addToBackStack("favorites")
//                            .commit()
//                    }
                }
            }
        }
    }

    override fun onDeleteLocationClick(location: FavoriteLocation) {
        viewModel.remove(location)
    }

    override fun onFavoriteLocationClick(location: FavoriteLocation) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToFavoriteFragment(location)
        findNavController(theView).navigate(action)
    }

}