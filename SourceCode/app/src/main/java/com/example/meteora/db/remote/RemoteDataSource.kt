package com.example.meteora.db.remote

import com.example.meteora.model.Weather
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String): Response<Weather>
}