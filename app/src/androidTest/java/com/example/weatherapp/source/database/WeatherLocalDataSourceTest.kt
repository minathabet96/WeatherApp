package com.example.weatherapp.source.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.weatherapp.database.AppDatabase
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
class WeatherLocalDataSourceTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var localDataSource: WeatherLocalDataSource
    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).build()
        localDataSource = WeatherLocalDataSource(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown(){
        database.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertAndRetrieve_locationWithNameLondon_theSameLocation() {
        runTest {
            //GIVEN
            val favoriteLocation = FavoriteLocation(name = "London", 0.0, 0.0)
            localDataSource.addToFavorites(favoriteLocation)

            //WHEN
            val result = localDataSource.getAllFavorites()
            advanceUntilIdle()
            val locationList= result.first()

            //THEN
            assertThat(locationList, `is`(notNullValue()))
            assertThat(locationList[0].name, `is`("London"))

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeLocation_twoInputLocations_sizeOfOne() {
        runTest {
            //GIVEN
            val location1 = FavoriteLocation("sample1", 0.0, 0.0)
            val location2 = FavoriteLocation("sample2", 1.0, 1.0)
            localDataSource.addToFavorites(location1)
            localDataSource.addToFavorites(location2)

            //WHEN
            val result = localDataSource.getAllFavorites()
            advanceUntilIdle()
            localDataSource.removeFromFavorites(location1)
            val locationList= result.first()

            //THEN
            assertThat(locationList, `is`(notNullValue()))
            assertThat(locationList.size, `is`(1))
            assertThat(locationList[0].name, `is`("sample2"))
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertAndRetrieve_alertWithIdAlertID_theSameAlert() {
        runTest {
            //GIVEN
            val alert = Alert(name = "London", 0.0, 0.0, "alertID", 1000)
            localDataSource.addToAlerts(alert)

            //WHEN
            val result = localDataSource.getAllAlerts()
            advanceUntilIdle()
            val alertsList= result.first()

            //THEN
            assertThat(alertsList, `is`(notNullValue()))
            assertThat(alertsList[0].id, `is`("alertId"))

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeAlert_twoInputAlerts_sizeOfOne() {
        runTest {
            //GIVEN
            val alert1 = Alert("sample1", 0.0, 0.0, "id1", 1000)
            val alert2 = Alert("sample2", 1.0, 1.0, "id2", 2000)
            localDataSource.addToAlerts(alert1)
            localDataSource.addToAlerts(alert2)

            //WHEN
            val result = localDataSource.getAllAlerts()
            advanceUntilIdle()
            localDataSource.removeFromAlerts(alert2)
            val alertsList= result.first()

            //THEN
            assertThat(alertsList, `is`(notNullValue()))
            assertThat(alertsList.size, `is`(1))
            assertThat(alertsList[0].id, not("id2"))
        }
    }
}