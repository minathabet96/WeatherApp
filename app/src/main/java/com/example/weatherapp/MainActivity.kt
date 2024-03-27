package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherapp.alerts.view.AlertsFragment
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.Alert

import com.example.weatherapp.utils.Communicator

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
        }
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
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

//    @SuppressLint("RestrictedApi")
//    override fun onStart() {
//    super.onStart()
//
//    navController.addOnDestinationChangedListener { controller, destination, _ ->
//        if (destination.id == R.id.alertsFragment) {
//            // Access the AlertsFragment here
//            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//            alertsFragment =
//                navHostFragment!!.childFragmentManager.findFragmentById(R.id.alertsFragment) as AlertsFragment
//            println(": ${alertsFragment.id}")
//        }
//    }
//}
//                    navController.navigate(R.id.alertsFragment)
//                    //val x = navController.currentBackStack.value[2]
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//        val alertsFragment = navHostFragment?.childFragmentManager?.findFragmentById(R.id.alertsFragment) as? AlertsFragment
//
//        println(": ${alertsFragment?.id}")
//        val navHost =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) //as AlertsFragment
//                    .let {
//                println("yeahh")
//                alertsFragment = it as AlertsFragment
//            println("its actual id: ${R.id.alertsFragment}")
//            println("it should be an id of: ${alertsFragment.id}")
//        }
    //                    val date = Date(x!!.list[0].dt * 1000)
//                    val calendar =
//                        Calendar.getInstance().apply { time = date }
//                    val dayName = calendar.get(Calendar.DAY_OF_WEEK)
//                        //for (item in x.list)
//                        for(item in x.list)
//                            println(item.weather[0].main)
//                }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.closeDrawer(
                GravityCompat.START
            ) else binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun receiveData(alert: Alert) {
//        alertsFragment.receiveData(alert)
//    }
}


