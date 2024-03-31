package com.example.weatherapp.utils

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.app.job.JobInfo.PRIORITY_MAX
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.media.SubtitleData
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.PermissionChecker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.databinding.SmallNotificationLayoutBinding
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weatherapp.home.viewModel.HomeViewModel
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.WeatherService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.lang.Exception

lateinit var viewModel: HomeViewModel

class MyWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        println("init")
        val lat = inputData.getString("lat")
        val lon = inputData.getString("lon")
        val name = inputData.getString("name") + " weather"
        println("lat: $lat")
        println("lon: $lat")
        val retro = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(WeatherService::class.java)
        val response = retro.getCurrentWeatherStats(lat!!, lon!!, KEY, "en", "metric")
        println("response code: ${response.code()}")
        val body = "Temperature: ${response.body()?.main?.temp}°C\n${response.body()?.weather?.get(0)?.description}"
        sendNotification(1000, name, body)
        return Result.success()
    }

    private fun sendNotification(id: Int, title: String, body: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("1000", id)
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationLayout =
            RemoteViews("com.example.weatherapp", R.layout.small_notification_layout)
        notificationLayout.setTextViewText(R.id.notification_title, title)
        val notificationLayoutExpanded =
            RemoteViews("com.example.weatherapp", R.layout.large_notification_layout)
        notificationLayoutExpanded.setTextViewText(R.id.notification_title, title)
        notificationLayoutExpanded.setTextViewText(R.id.notification_body, body)


        val pendingIntent = getActivity(applicationContext, 0, intent, FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(applicationContext, "channel")
            .setSmallIcon(R.drawable.weatherapp)
            .setDefaults(0).setAutoCancel(true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .addAction(0, "open WeatherApp", pendingIntent)
        notification.priority = PRIORITY_MAX

        notification.setChannelId("channel")

        val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
            .setContentType(CONTENT_TYPE_SONIFICATION).build()

        val channel =
            NotificationChannel("channel", "TEST", IMPORTANCE_HIGH)

        channel.enableLights(true)
        channel.lightColor = RED
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        channel.setSound(ringtoneManager, audioAttributes)
        notificationManager.createNotificationChannel(channel)

        if (PermissionChecker.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            println("NOTIFICATIONS PERMISSION NOT GRANTED")
        } else {
            println("PERMISSIONS GRANTED")
            notificationManager.notify(id, notification.build())
        }
    }

//    private suspend fun requestPermissions(permissions: Array<String>) {
//        withContext(Dispatchers.Main) {
//            ActivityCompat.requestPermissions(
//                applicationContext as Activity,
//                permissions,
//                1000
//            )
//        }
//    }
}
