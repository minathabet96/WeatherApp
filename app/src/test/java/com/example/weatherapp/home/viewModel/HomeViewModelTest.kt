package com.example.weatherapp.home.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.FakeRepository

import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.FiveDayForecast
import com.example.weatherapp.utils.ApiResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class HomeViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeRepository: FakeRepository
    private lateinit var favoritesList: MutableList<FavoriteLocation>
    private lateinit var alertsList: MutableList<Alert>

    @Before
    fun setup() {
        favoritesList = mutableListOf()
        alertsList = mutableListOf()
        fakeRepository = FakeRepository(favoritesList, alertsList)
        viewModel = HomeViewModel(fakeRepository)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getWeatherForToday_currentWeather() {
        runBlockingTest {

            viewModel.getWeatherForToday("", "", "", "")
            delay(100)
            val res = viewModel.currentWeatherStateFlow.first()
            val result = res as ApiResponse.Success
            assertThat(result.data, instanceOf(CurrentWeather::class.java))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getFiveDayForecast_fiveDayForecast() {
        runBlockingTest {

            viewModel.getFiveDayForecast("", "", "", "")
            delay(100)
            val res = viewModel.fiveDayForecastStateFlow.first()
            val result = res as ApiResponse.Success
            assertThat(result.data, instanceOf(FiveDayForecast::class.java))
        }
    }
}