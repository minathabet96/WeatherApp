package com.example.weatherapp.alerts.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityDiagnosticsManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapp.R
import com.example.weatherapp.utils.MyWorker
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.WeatherRemoteDataSource
import com.example.weatherapp.model.WeatherRepository
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

class AlertsFragment : Fragment() {
    private lateinit var binding: FragmentAlertsBinding
    private lateinit var alertsAdapter: AlertsAdapter
    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(
            requireActivity(), FavoritesViewModelFactory(
                (WeatherRepository.getInstance(
                    WeatherRemoteDataSource(),
                    WeatherLocalDataSource(requireContext())
                ))
            )
        )[FavoritesViewModel::class.java]
        alertsAdapter = AlertsAdapter { alert ->
            showDialog(
                requireContext(),
                "Delete Alarm",
                "Are you sure you want to delete this alarm?",
                "Delete",
                "Cancel",
                { _, _ ->
                    WorkManager.getInstance(requireContext())
                        .cancelWorkById(UUID.fromString(alert.id))
                    viewModel.removeFromAlerts(alert)
                }) { dialog, _ -> dialog.dismiss() }

        }
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllAlerts()
        lifecycleScope.launch {
            viewModel.alertsStateFlow.collect { list ->
                val filteredList = list.filter {
                    it.time > System.currentTimeMillis()
                }
                for (alert in list) {
                    if (alert.time < System.currentTimeMillis())
                        println("alert's time: ${alert.time} and current time: ${System.currentTimeMillis()}")
                }
                if (filteredList.isEmpty()) {
                    binding.noAlerts.visibility = View.VISIBLE
                    binding.alertsRecycler.visibility = View.GONE
                }
                else {
                    binding.noAlerts.visibility = View.GONE
                    binding.alertsRecycler.visibility = View.VISIBLE
                }
                binding.alertsRecycler.apply {
                    alertsAdapter.submitList(filteredList)
                    adapter = alertsAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }

            }
        }
        if (viewModel.alert.name.isNotEmpty())
            setAlert(viewModel.alert)
        binding.alertsFab.setOnClickListener {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(connectivityManager.activeNetwork==null)
                Snackbar.make(requireView(), R.string.no_connection, Snackbar.ANIMATION_MODE_FADE).show()
            else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        android.Manifest.permission.POST_NOTIFICATIONS,
                        android.Manifest.permission.ACCESS_NOTIFICATION_POLICY
                    ), 0
                )
                if (hasPermission()) {
                    val action = AlertsFragmentDirections.actionAlertsFragmentToMapsFragment(false)
                    findNavController(view).navigate(action)
                } else {
                    requestPermission()
                }
            }
        }
    }

    private fun setAlert(alert: Alert) {
        val dateConstraints =
            CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker
            .Builder.datePicker()
            .setTitleText("Select date")
            .setCalendarConstraints(dateConstraints.build())
            .build()

        val timePicker = MaterialTimePicker
            .Builder()
            .setTitleText("select time")
            .build()

        datePicker.show(childFragmentManager, "datePicker")
        datePicker.addOnPositiveButtonClickListener {
            timePicker.show(childFragmentManager, "timePicker")
        }
        timePicker.addOnPositiveButtonClickListener {
            val selectedTime = addHoursAndMinutesToTimestamp(
                datePicker.selection!!,
                timePicker.hour,
                timePicker.minute
            ) - 7200000
            if (selectedTime < System.currentTimeMillis()) {
                println("INVALID SELECTION!!!")
            } else {
                val request = OneTimeWorkRequestBuilder<MyWorker>().setInitialDelay(
                    selectedTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS
                )
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val inputData = Data.Builder()
                inputData.putString("lat", alert.lat.toString())
                inputData.putString("lon", alert.lon.toString())
                inputData.putString("name", alert.name)

                request.setInputData(inputData.build())
                request.setConstraints(constraints)
                val builtRequest = request.build()
                WorkManager.getInstance(requireContext()).cancelWorkById(builtRequest.id)
                alert.id = builtRequest.id.toString()
                alert.time = selectedTime
                viewModel.addToAlerts(alert)
                WorkManager.getInstance(requireContext()).enqueue(builtRequest)
            }

        }
    }


    private fun addHoursAndMinutesToTimestamp(timestamp: Long, hours: Int, minutes: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.add(Calendar.HOUR, hours)
        calendar.add(Calendar.MINUTE, minutes)
        return calendar.timeInMillis
    }

    private fun hasPermission(): Boolean {
        return Settings.canDrawOverlays(requireContext())
    }

    private fun showDialog(
        ctx: Context, title: String, message: String,
        posString: String, negString: String,
        posListener: DialogInterface.OnClickListener, negListener: DialogInterface.OnClickListener
    ) {
        MaterialAlertDialogBuilder(ctx)
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(posString, posListener)
            .setNegativeButton(negString, negListener)
            .show()
    }

    private fun requestPermission() {

        showDialog(
            requireContext(), "Permission Needed", "Please enable the app to show alerts.",
            "Enable", "Dismiss",
            { _, _ ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package: com.example.weatherapp")
                )
                requireActivity().startActivityForResult(intent, 1000)
            },
            { dialog, _ -> dialog.dismiss() },
        )
    }
}



