package com.example.weatherapp.settings.view

import android.content.Intent.getIntent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.weatherapp.R
import com.example.weatherapp.utils.DataStoreUtil
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.settings.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

//        lifecycleScope.launch {
//            val preferences = dataStore.data.first()
//            println("reading from datastore: ${preferences[temp]}")
//        }
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            dataStore.getTemp().collect {
                println("temp unit is: $it")
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
                println("lang unit is: $it")
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
                println("wind unit unit is: $it")
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
            delay(500)
            setSettings()
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        viewModel.setLocale(languageCode)
        lifecycleScope.launch {
            viewModel.getLocale().collect {
                println("language actually is $it")
            }
        }
    }

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
            when (group.checkedRadioButtonId) {
                binding.engBtn.id -> {
                    setLocale("en")
                    activity?.finish()
                    startActivity(activity?.intent!!)
                }

                binding.arabicBtn.id -> {
                    setLocale("ar")
                    activity?.finish()
                    startActivity(activity?.intent!!)
                }
            }
        }
        binding.windRadioGroup.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                binding.msBtn.id -> viewModel.setWindSpeedUnit("metric")
                binding.mhBtn.id -> viewModel.setWindSpeedUnit("imperial")
            }
        }
    }
}


