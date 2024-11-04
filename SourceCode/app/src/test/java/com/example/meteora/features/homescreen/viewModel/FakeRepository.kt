package com.example.meteora.features.homescreen.viewModel

import com.example.meteora.db.repository.Repository
import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : Repository {

    var weatherResponse: Weather? = null
    var forecastResponse: Forcast? = null

    override fun fetchCurrentWeather(lat: Double, lon: Double, windSpeedUnit: String, language: String): Flow<Weather> {
        return flow {
            weatherResponse?.let { emit(it) } ?: emit(Weather()) // Emit the mock weather or a default empty Weather
        }
    }

    override fun fetchForecast(lat: Double, lon: Double, windSpeedUnit: String, language: String): Flow<Forcast> {
        return flow {
            forecastResponse?.let { emit(it) } ?: emit(Forcast()) // Return the mock or empty Forecast
        }
    }

    override suspend fun insertForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllForecast(): List<Forcast> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }

    override suspend fun updateForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }
}
