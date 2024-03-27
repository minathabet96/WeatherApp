package com.example.weatherapp.source.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.weatherapp.database.AppDatabase
import com.example.weatherapp.database.WeatherDao
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
class WeatherDaoTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: WeatherDao
    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).build()

        dao = database.weatherDAO
    }

    @After
    fun tearDown(){
        database.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertAndRetrieveLocation_locationWithNameLondon_theSameLocation() {
        runBlockingTest {
            //GIVEN
            val favoriteLocation = FavoriteLocation(name = "London", 0.0, 0.0)
            dao.insertIntoFavorites(favoriteLocation)

            //WHEN
            val result = dao.getAllFavorites()
            advanceUntilIdle()
            val locationList= result.first()

            //THEN
            assertThat(locationList, `is`(notNullValue()))
            assertThat(locationList[0].name, `is`("London"))
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteLocation_twoInputLocations_sizeOfOne() {
        runBlockingTest {
            //GIVEN
            val location1 = FavoriteLocation("sample1", 0.0, 0.0)
            val location2 = FavoriteLocation("sample2", 1.0, 1.0)
            dao.insertIntoFavorites(location1)
            dao.insertIntoFavorites(location2)

            //WHEN
            dao.deleteFromFavorites(location2)
            val result = dao.getAllFavorites()
            advanceUntilIdle()
            val locationList= result.first()

            //THEN
            assertThat(locationList, `is`(notNullValue()))
            assertThat(locationList.size, `is`(1))
        }
    }
}