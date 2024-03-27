package com.example.weatherapp.home.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.AlertResponse
import com.example.weatherapp.utils.ApiResponse
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.FiveDayForecast
import com.example.weatherapp.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: WeatherRepository): ViewModel() {
    private val _alertStateFlow: MutableStateFlow<ApiResponse<AlertResponse>> = MutableStateFlow(ApiResponse.Loading())
    val alertStateFlow: StateFlow<ApiResponse<AlertResponse>> = _alertStateFlow.asStateFlow()

    private val _currentWeatherStateFlow: MutableStateFlow<ApiResponse<CurrentWeather>> = MutableStateFlow(ApiResponse.Loading())
    val currentWeatherStateFlow: StateFlow<ApiResponse<CurrentWeather>> = _currentWeatherStateFlow.asStateFlow()

    private val _fiveDayForecastStateFlow: MutableStateFlow<ApiResponse<FiveDayForecast>> = MutableStateFlow(ApiResponse.Loading())
    val fiveDayForecastStateFlow: StateFlow<ApiResponse<FiveDayForecast>> = _fiveDayForecastStateFlow.asStateFlow()
    fun getWeatherForToday(lat: String, lon: String, lang: String, units: String) {
        val flow = repo.getCurrentWeatherStats(lat, lon, lang, units)
        viewModelScope.launch(Dispatchers.IO) {
            flow
                .catch { _currentWeatherStateFlow.value = ApiResponse.Failure(it) }
                .collect { _currentWeatherStateFlow.value = ApiResponse.Success(it) }
        }
    }

    fun getFiveDayForecast(lat: String, lon: String, lang: String, units: String) {
        val flow = repo.getWeekWeatherStats(lat, lon, lang, units)
        viewModelScope.launch(Dispatchers.IO) {
            flow
                .catch { _fiveDayForecastStateFlow.value = ApiResponse.Failure(it) }
                .collect { _fiveDayForecastStateFlow.value = ApiResponse.Success(it) }
        }
    }
    fun getAlert(lat: String, lon: String){
        val flow = repo.getAlert(lat, lon)
        viewModelScope.launch(Dispatchers.IO) {
            flow
                .catch { _alertStateFlow.value = ApiResponse.Failure(it) }
                .collect { _alertStateFlow.value = ApiResponse.Success(it) }
        }
    }
}
//                    for (step in 0 until 8) {
//                        println(apiResponse!!.list[step].weather[0].description)
//                        val date = formatDate(apiResponse.list[step].dt)
//                        println("date: $date")
