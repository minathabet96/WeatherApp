package com.example.weatherapp.model

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest(){
    private  lateinit var remoteDataSource: FakeRemoteDataSource
    private  lateinit var localDataSource: FakeLocalDataSource
    private  lateinit var repo: WeatherRepository

    @Before
    fun setup(){

        remoteDataSource = FakeRemoteDataSource()
        localDataSource = FakeLocalDataSource()
        repo = WeatherRepository.getInstance(remoteDataSource, localDataSource)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addToAndRetrieveFromFavorites_locationWithNameTestLocation_TheSameLocation(){
        //GIVEN
        val location = FavoriteLocation("testLocation", 0.0, 0.0)

        runBlockingTest {
            //WHEN
            repo.addToFavorites(location)
            val result = repo.getAllFavorites()

            //THEN
            result.collect {
                assertThat(it, `is`(notNullValue()))
                assertThat(it[0].name, `is`("testLocation"))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeLocation_twoInputLocations_sizeOfOne() {
        runBlockingTest {
            //GIVEN
            val location1 = FavoriteLocation("sample1", 0.0, 0.0)
            val location2 = FavoriteLocation("sample2", 1.0, 1.0)
            repo.addToFavorites(location1)
            repo.addToFavorites(location2)

                //WHEN
                val result = repo.getAllFavorites()
                repo.removeFromFavorites(location1)
                val locationList = result.first()

                //THEN
                assertThat(locationList, `is`(notNullValue()))
                assertThat(locationList.size, `is`(1))
                assertThat(locationList[0].name, `is`("sample2"))
            }
    }
    @Test
    fun addToRetrieveFromAlerts_alertWithNameTestAlert_TheSameAlert(){
        //GIVEN
        val alert = Alert("testAlert", 0.0, 0.0, "id", 1000)

        runBlockingTest {
            //WHEN
            repo.addToAlerts(alert)
            val result = repo.getAllAlerts()

            //THEN
            result.collect {
                assertThat(it, `is`(notNullValue()))
                assertThat(it[0].name, `is`("testAlert"))
            }
        }
    }

    @Test
    fun removeAlert_twoInputAlerts_sizeOfOne() {
        runBlockingTest {
            //GIVEN
            val alert1 = Alert("alert1", 0.0, 0.0, "id1", 1100)
            val alert2 = Alert("alert2", 1.0, 1.0, "id2", 1200)
            repo.addToAlerts(alert1)
            repo.addToAlerts(alert2)

            //WHEN
            val result = repo.getAllAlerts()
            repo.removeFromAlerts(alert1)
            val alertsList = result.first()

            //THEN
            assertThat(alertsList, `is`(notNullValue()))
            assertThat(alertsList.size, `is`(1))
            assertThat(alertsList[0].name, `is`("alert2"))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getCurrentWeatherStats_CurrentWeatherStats(){
        runBlockingTest {
            val result = repo.getCurrentWeatherStats("", "", "", "")
            result.collect {
                assertThat(result, `is`(notNullValue()))
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getWeekWeatherStats_WeekWeatherStats(){
        runBlockingTest {
            val result = repo.getWeekWeatherStats("", "", "", "")
            result.collect {
                assertThat(result, `is`(notNullValue()))
            }
        }
    }
}