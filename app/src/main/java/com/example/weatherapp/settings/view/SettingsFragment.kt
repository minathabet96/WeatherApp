package com.example.weatherapp.settings.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.utils.DataStoreUtil
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.model.HomeLocation
import com.example.weatherapp.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.settings.viewmodel.SettingsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var dataStore: DataStoreUtil


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dataStore = DataStoreUtil.getInstance(requireContext())
        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(requireContext())
        )[SettingsViewModel::class.java]

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            dataStore.getTemp().collect {
                when (it) {
                    "metric" -> {
                        binding.celsiusBtn.isChecked = true
                    }

                    "imperial" -> {
                        binding.fehrenheitBtn.isChecked = true
                    }

                    "standard" -> {
                        binding.kelvinBtn.isChecked = true
                    }
                }
            }
        }
        lifecycleScope.launch {
            dataStore.getLang().collect {
                when (it) {
                    "ar" -> {
                        binding.arabicBtn.isChecked = true
                    }

                    "en" -> {
                        binding.engBtn.isChecked = true
                    }
                }
            }
        }
        lifecycleScope.launch {
            dataStore.getWindUnit().collect {
                when (it) {
                    "metric" -> {
                        binding.msBtn.isChecked = true
                    }

                    "imperial" -> {
                        binding.mhBtn.isChecked = true
                    }
                }
            }
        }
        lifecycleScope.launch {
//            viewModel.getHomeLocation().collect{
//                val homeLocation = Gson().fromJson(it, HomeLocation::class.java)
//                when (homeLocation.name) {
//                    "null" -> binding.gpsBtn.isChecked = true
//                    else -> binding.mapBtn.isChecked = true
//            }
            dataStore.getLocationSetting().collect {
                when (it) {
                    "gps" -> binding.gpsBtn.isChecked = true
                    "maps" -> binding.mapBtn.isChecked = true
                }

            }
        }
        lifecycleScope.launch {
            delay(200)
            setSettings()
        }
    }
    @SuppressLint("RestrictedApi")
    private fun setSettings() {
        binding.tempRadioGroup.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                binding.kelvinBtn.id -> {
                    viewModel.setTempUnit("standard")
                }

                binding.celsiusBtn.id -> {
                    viewModel.setTempUnit("metric")
                }

                binding.fehrenheitBtn.id -> {
                    viewModel.setTempUnit("imperial")
                }
            }
        }
        binding.languageRadioGroup.setOnCheckedChangeListener { group, _ ->
            val sharedPref = requireContext().getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()



            when (group.checkedRadioButtonId) {
                binding.engBtn.id -> {
                    editor.putString("lang", "en")
                    editor.apply()
                    viewModel.setLocale("en")
                    val lang = sharedPref.getString("lang", "en")
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                    //setLocale("en")
                }

                binding.arabicBtn.id -> {
                    editor.putString("lang", "ar")
                    editor.apply()
                    viewModel.setLocale("ar")
                    val lang = sharedPref.getString("lang", "ar")
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                    //setLocale("ar")
                }
            }
        }
        binding.windRadioGroup.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                binding.msBtn.id -> viewModel.setWindSpeedUnit("metric")
                binding.mhBtn.id -> viewModel.setWindSpeedUnit("imperial")
            }
        }
        binding.locationRadioGroup.setOnCheckedChangeListener { group, _ ->
            findNavController().popBackStack(R.id.settingsFragment, false)
            when (group.checkedRadioButtonId) {
                binding.gpsBtn.id -> {
                    viewModel.setLocation("gps")
                    //viewModel.setHomeLocation(HomeLocation("null", "", ""))
                }

                binding.mapBtn.id -> {
                    val connectivityManager =
                        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    if (connectivityManager.activeNetwork == null) {
                        binding.gpsBtn.isChecked = true
                        Snackbar.make(requireView(), R.string.no_connection, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else {
                        viewModel.setLocation("maps")
                        val action = SettingsFragmentDirections.actionSettingsFragmentToMapsFragment(false, true)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }
}


