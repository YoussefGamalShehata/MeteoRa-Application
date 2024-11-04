package com.example.meteora.features.homescreen.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.meteora.model.CurrentClouds
import com.example.meteora.model.CurrentCoord
import com.example.meteora.model.CurrentMain
import com.example.meteora.model.CurrentRain
import com.example.meteora.model.CurrentSys
import com.example.meteora.model.CurrentWind
import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiState
import com.example.meteora.ui.home.HomeViewModel
import com.example.skycast.ViewModelRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fakeRepository: FakeRepository
    private lateinit var context: Context

    @get:Rule
    val viewModelRule = ViewModelRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        context = ApplicationProvider.getApplicationContext()
        fakeRepository = FakeRepository()
        homeViewModel = HomeViewModel(fakeRepository, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchCurrentWeather_updatesWeatherData() = runTest {
        // Given
        val latitude = 0.0
        val longitude = 0.0
        val mockCurrentWeather = Weather(
            coord = CurrentCoord(lon = 0.0, lat = 0.0),
            weather = listOf(),
            base = "",
            main = CurrentMain(temp = 0.0, feelsLike = 0.0, tempMin = 0.0, tempMax = 0.0, pressure = 0, humidity = 0, seaLevel = 0, grndLevel = 0),
            visibility = 0,
            wind = CurrentWind(speed = 0.0, deg = 0, gust = 0.0),
            rain = CurrentRain(n1h = 0.0),
            clouds = CurrentClouds(all = 0),
            dt = 0,
            sys = CurrentSys(country = "", sunrise = 0, sunset = 0),
            timezone = 0,
            id = 0,
            name = "",
            cod = 0
        )
        fakeRepository.weatherResponse = mockCurrentWeather

        // When
        homeViewModel.fetchCurrentWeather(latitude, longitude)
        advanceUntilIdle()

        // Then
        val weatherData = homeViewModel.weatherData.value
        assertThat(weatherData, equalTo(ApiState.Success(mockCurrentWeather)))
    }


    @Test
    fun fetchForecast_updatesForecastData() = runTest {
        // Given
        val mockForecast = Forcast()
        fakeRepository.forecastResponse = mockForecast

        // When
        homeViewModel.fetchForecast(40.7128, -74.0060)
        advanceUntilIdle()
        val forecastData = homeViewModel.forecastData.value
        // Then
        assertThat(forecastData, equalTo(ApiState.Success(mockForecast)))
    }

    @Test
    fun fetchCurrentWeather_handlesError() = runTest {
        // Given
        fakeRepository.weatherResponse = null

        // When
        homeViewModel.fetchCurrentWeather(40.7128, -74.0060)
        advanceUntilIdle()
        val weatherData = homeViewModel.weatherData.value
        // Then
        assertThat(weatherData is ApiState.Failure, equalTo(true))
    }

    @Test
    fun fetchForecast_handlesError() = runTest {
        // Given
        fakeRepository.forecastResponse = null

        // When
        homeViewModel.fetchForecast(40.7128, -74.0060)
        advanceUntilIdle()
        val forecastData = homeViewModel.forecastData.value
        // Then
        assertThat(forecastData is ApiState.Failure, equalTo(true))
    }
}
