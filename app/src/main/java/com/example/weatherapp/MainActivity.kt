package com.example.weatherapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.settings.viewmodel.SettingsViewModelFactory
import com.example.weatherapp.utils.DataStoreUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

//KEY: 8084bd2c7fd59779ef2676212ff216e8
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                    binding = ActivityMainBinding.inflate(layoutInflater)
                    setContentView(binding.root)
                    supportActionBar?.apply {
                        setHomeAsUpIndicator(R.drawable.menu)
                        setDisplayShowHomeEnabled(true)
                        setDisplayHomeAsUpEnabled(true)
                        setTitle(R.string.app_name)
                    }
                    navController =
                        Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                    NavigationUI.setupWithNavController(binding.navigationView, navController)
                    binding.navigationView.setNavigationItemSelectedListener {
                        if (supportFragmentManager.backStackEntryCount > 0) {
                            val fragmentId =
                                supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).id
                            if (fragmentId == R.id.favoriteFragment)
                                supportFragmentManager.popBackStack()
                        }
                        when (it.itemId) {
                            R.id.homeFragment -> navController.navigate(R.id.homeFragment)
                            R.id.favoritesFragment -> navController.navigate(R.id.favoritesFragment)
                            R.id.settingsFragment -> navController.navigate(R.id.settingsFragment)

                            R.id.alertsFragment -> navController.navigate(R.id.alertsFragment)
                        }
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }
                }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.closeDrawer(
                GravityCompat.START
            ) else binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
}


