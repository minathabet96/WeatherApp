package com.example.weatherapp.source.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.weatherapp.database.AppDatabase
import com.example.weatherapp.database.WeatherLocalDataSource
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
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
            localDataSource.add(favoriteLocation)

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
            localDataSource.add(location1)
            localDataSource.add(location2)

            //WHEN
            val result = localDataSource.getAllFavorites()
            advanceUntilIdle()
            localDataSource.remove(location1)
            val locationList= result.first()

            //THEN
            assertThat(locationList, `is`(notNullValue()))
            assertThat(locationList.size, `is`(1))
        }
    }
}