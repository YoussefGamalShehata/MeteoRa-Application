package com.example.meteora.db.local

import com.example.meteora.model.Forcast

interface LocalDataSource {
    suspend fun insertForecast(forecast: Forcast)
    suspend fun getAllForecast(): List<Forcast>
    suspend fun deleteForecast(forecast: Forcast)
    suspend fun updateForecast(forecast: Forcast)

}