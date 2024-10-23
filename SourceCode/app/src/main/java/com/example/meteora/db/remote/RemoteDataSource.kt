package com.example.meteora.db.remote

import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String,lang : String): Response<Weather>
    suspend fun getForecast(lat: Double, lon: Double, units: String,lang : String): Response<Forcast>

}