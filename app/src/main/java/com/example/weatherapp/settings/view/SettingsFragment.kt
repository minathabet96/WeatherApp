package com.example.weatherapp.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Utils.DataStoreUtil
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.settings.viewmodel.SettingsViewModelFactory

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var dataStore: DataStoreUtil


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, SettingsViewModelFactory(requireContext()))[SettingsViewModel::class.java]
//        val temp = stringPreferencesKey("temp")
//        val dataStore = requireActivity().dataStore
//        lifecycleScope.launch {
//            dataStore.edit { settings ->
//                settings[temp] = "celsius"
//
//            }
//        lifecycleScope.launch {
//            val preferences = dataStore.data.first()
//            println("reading from datastore: ${preferences[temp]}")
//        }
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tempRadioGroup.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                binding.kelvinBtn.id -> viewModel.setTemp("standard")
                binding.celsiusBtn.id -> viewModel.setTemp("metric")
                binding.fehrenheitBtn.id -> viewModel.setTemp("imperial")
            }
//            lifecycleScope.launch{
//                dataStore.edit {
//                    it[stringPreferencesKey("temp")] = "kelvin"
//                    println("the new value is" + dataStore.data.first()[stringPreferencesKey("temp")])
//                }
        }
    }
}


