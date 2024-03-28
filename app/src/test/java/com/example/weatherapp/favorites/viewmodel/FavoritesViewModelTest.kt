package com.example.weatherapp.favorites.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FakeRepository
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FavoritesViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var fakeRepository: FakeRepository
    private lateinit var favoritesList: MutableList<FavoriteLocation>
    private lateinit var alertsList: MutableList<Alert>

    @Before
    fun setup() {
        favoritesList = mutableListOf(
            FavoriteLocation("location1", 0.0, 0.0),
            FavoriteLocation("location2", 1.1, 1.1)
        )
        alertsList = mutableListOf(
            Alert("alert1", 0.0, 0.0, "id1", 1000),
            Alert("alert2", 0.0, 0.0, "id2", 2000)
        )
        fakeRepository = FakeRepository(favoritesList, alertsList)
        viewModel = FavoritesViewModel(fakeRepository)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addToFavorites_oneLocation_listSizeOfThree() {
        val location = FavoriteLocation("location3", 2.2, 3.3)

        runBlockingTest {
            viewModel.addToFavorites(location)
            delay(1000)
            assertThat(favoritesList.size, `is`(3))
        }
    }
    @Test
    fun removeFromFavorites_oneLocation_listSizeOfOne() {
        val location = FavoriteLocation("location1", 0.0, 0.0)
        runBlockingTest {
            viewModel.removeFromFavorites(location)
            delay(1000)
            assertThat(favoritesList.size, `is`(1))

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllFavorites_listOfSize2(){

        runBlockingTest {
            viewModel.getAllFavorites()
            val list = viewModel.favoritesStateFlow.first()
            assertThat(list.size, `is`(2))
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addToAlerts_oneAlert_listSizeOfThree() {
        val alert = Alert("alert3", 2.2, 3.3, "id3", 1000)

        runBlockingTest {
            viewModel.addToAlerts(alert)
            delay(500)
            assertThat(alertsList.size, `is`(3))

        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeFromAlerts_oneAlert_listSizeOfOne() {
        val alert = Alert("alert1", 0.0, 0.0, "id1", 1000)
        runBlockingTest {
            viewModel.removeFromAlerts(alert)
            delay(100)
            assertThat(alertsList.size, `is`(1))

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllAlerts_listOfSize2(){

        runBlockingTest {
            viewModel.getAllAlerts()
            val list = viewModel.alertsStateFlow.first()
            assertThat(list.size, `is`(2))
        }
    }
}