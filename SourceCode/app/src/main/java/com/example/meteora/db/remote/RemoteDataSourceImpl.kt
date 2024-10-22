package com.example.meteora.network

import com.example.meteora.db.remote.RemoteDataSource
import com.example.meteora.helpers.Constants
import com.example.meteora.model.Weather
import retrofit2.Response

class RemoteDataSourceImpl(private val apiService: ApiService) : RemoteDataSource {

    override suspend fun getCurrentWeather(lat: Double, lon: Double, units: String): Response<Weather> {
        return apiService.getCurrentWeather(lat, lon, Constants.API_KEY, units)
    }
}
