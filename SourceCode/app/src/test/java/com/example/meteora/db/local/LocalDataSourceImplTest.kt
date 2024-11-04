package com.example.meteora.db.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.meteora.model.Forcast
import com.example.meteora.model.ForecastCity
import com.example.meteora.model.ForecastClouds
import com.example.meteora.model.ForecastInfo
import com.example.meteora.model.ForecastMain
import com.example.meteora.model.ForecastSys
import com.example.meteora.model.ForecastWind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataSourceImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var forecastDao: ForecastDao
    private lateinit var localDataSource: LocalDataSourceImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        forecastDao = database.weatherDao()
        localDataSource = LocalDataSourceImpl(context)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertForecast_retrievesForecast() = runBlocking {
        // Given
        val forecast = Forcast(
            cod = "200",
            message = 0,
            cnt = 1,
            list = listOf(
                ForecastInfo(
                    dt = 1638205600, // Example timestamp
                    main = ForecastMain(temp = 25.0),
                    weather = listOf(),
                    clouds = ForecastClouds(),
                    wind = ForecastWind(),
                    visibility = 10000,
                    pop = 0.0,
                    rain = null,
                    sys = ForecastSys(),
                    dtTxt = "2021-11-28 12:00:00"
                )
            ),
            city = ForecastCity(name = "Test City")
        )

        // When
        localDataSource.insertForecast(forecast) // Direct call
        val allForecasts = localDataSource.getAllForecast() // Direct call

        // Then
        assertThat(allForecasts.size, `is`(1))
        assertThat(allForecasts[0].city.name, `is`("Test City"))
    }



    @Test
    fun deleteForecast_checkIfDeleted() = runBlocking {
        // Given
        val forecast = Forcast(
            cod = "200",
            message = 0,
            cnt = 1,
            list = listOf(
                ForecastInfo(
                    dt = 1638205600,
                    main = ForecastMain(temp = 25.0),
                    weather = listOf(),
                    clouds = ForecastClouds(),
                    wind = ForecastWind(),
                    visibility = 10000,
                    pop = 0.0,
                    rain = null,
                    sys = ForecastSys(),
                    dtTxt = "2021-11-28 12:00:00"
                )
            ),
            city = ForecastCity(name = "Test City")
        )
        // When
        localDataSource.insertForecast(forecast)
        localDataSource.deleteForecast(forecast)
        val allForecasts = localDataSource.getAllForecast()
        // Then
        assertThat(allForecasts.contains(forecast), `is`(false))
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllForecast_retrievesAllForecasts() = runBlocking {
        // Given
        val forecast1 = Forcast(
            cod = "200",
            message = 0,
            cnt = 1,
            list = listOf(
                ForecastInfo(
                    dt = 1638205600,
                    main = ForecastMain(temp = 25.0),
                    weather = listOf(),
                    clouds = ForecastClouds(),
                    wind = ForecastWind(),
                    visibility = 10000,
                    pop = 0.0,
                    rain = null,
                    sys = ForecastSys(),
                    dtTxt = "2021-11-28 12:00:00"
                )
            ),
            city = ForecastCity(name = "City 1")
        )
        val forecast2 = Forcast(
            cod = "200",
            message = 0,
            cnt = 1,
            list = listOf(
                ForecastInfo(
                    dt = 1638205600,
                    main = ForecastMain(temp = 30.0),
                    weather = listOf(),
                    clouds = ForecastClouds(),
                    wind = ForecastWind(),
                    visibility = 10000,
                    pop = 0.0,
                    rain = null,
                    sys = ForecastSys(),
                    dtTxt = "2021-11-28 12:00:00"
                )
            ),
            city = ForecastCity(name = "City 2")
        )

        // When
        localDataSource.insertForecast(forecast1)
        localDataSource.insertForecast(forecast2)
        val result = localDataSource.getAllForecast()
        // Then
        assertThat(result.size, `is`(2))
        assertThat(result.map { it.city.name }, `is`(listOf("City 1", "City 2")))
    }
}
