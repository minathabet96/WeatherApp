package com.example.weatherapp.utils
import com.example.weatherapp.model.Alert

interface Communicator {
    fun receiveData(alert: Alert)
}