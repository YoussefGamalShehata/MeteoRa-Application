package com.example.meteora.db.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.meteora.model.Forcast
import com.example.meteora.model.ForecastCity
import com.example.meteora.model.ForecastCoord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ForecastDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: ForecastDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.weatherDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertForecast_andGetAllForecast() = runTest {
        //Given
        val forecast = Forcast(
            key = 1,
            cod = "200",
            message = 0L,
            cnt = 1L,
            list = emptyList(),
            city = ForecastCity(id = 1, name = "Test City", coord = ForecastCoord(0.0, 0.0), country = "Test Country", population = 0, timezone = 0, sunrise = 0, sunset = 0)
        )

        // When Insert forecast
        dao.insertForecast(forecast)
        // Retrieve all forecasts
        val allForecasts = dao.getAllForecast()

        // Then Check that the inserted forecast is in the list
        assertThat(allForecasts.size, `is`(1))
        assertThat(allForecasts[0], `is`(forecast))
    }

    @Test
    fun updateForecast_andGetById() = runTest {
        //Given
        val forecast = Forcast(
            key = 1,
            cod = "200",
            message = 0L,
            cnt = 1L,
            list = emptyList(),
            city = ForecastCity(id = 1, name = "Test City", coord = ForecastCoord(0.0, 0.0), country = "Test Country", population = 0, timezone = 0, sunrise = 0, sunset = 0)
        )
        //When
        dao.insertForecast(forecast)
        val updatedForecast = forecast.copy(cod = "404", cnt = 2L)
        dao.updateForecast(updatedForecast)
        val allForecasts = dao.getAllForecast()
        //Then
        assertThat(allForecasts.size, `is`(1))
        assertThat(allForecasts[0].cod, `is`("404"))
        assertThat(allForecasts[0].cnt, `is`(2L))
    }

    @Test
    fun deleteForecast_andCheckIfDeleted() = runTest {
        //Given
        val forecast = Forcast(
            key = 1,
            cod = "200",
            message = 0L,
            cnt = 1L,
            list = emptyList(),
            city = ForecastCity(id = 1, name = "Test City", coord = ForecastCoord(0.0, 0.0), country = "Test Country", population = 0, timezone = 0, sunrise = 0, sunset = 0)
        )
        //When
        dao.insertForecast(forecast)
        dao.deleteForecast(forecast)
        val allForecasts = dao.getAllForecast()

        // Then Check that the forecast was deleted
        assertThat(allForecasts.isEmpty(), `is`(true))
    }
}
