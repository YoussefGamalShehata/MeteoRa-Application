package com.example.meteora.db.repository

import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiState
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun fetchCurrentWeather(lat: Double, long: Double, units: String,lang : String):  Flow<Weather>
    fun fetchForecast(lat: Double, long: Double, units: String,lang : String):  Flow<Forcast>
    suspend fun insertForecast(forecast: Forcast)
    suspend fun getAllForecast(): List<Forcast>
    suspend fun deleteForecast(forecast: Forcast)
    suspend fun updateForecast(forecast: Forcast)
}