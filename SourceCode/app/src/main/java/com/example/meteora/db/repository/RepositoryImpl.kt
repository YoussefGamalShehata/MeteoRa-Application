package com.example.meteora.db.repository

import com.example.meteora.db.remote.RemoteDataSource
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiClient
import com.example.meteora.network.RemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : Repository {

    override fun fetchCurrentWeather(lat: Double, lon: Double, units: String): Flow<Weather> = flow {
        val response = remoteDataSource.getCurrentWeather(lat, lon, units)

        if (response.isSuccessful) {
            response.body()?.let { weather ->
                emit(weather)  // Emit the weather data
            } ?: run {
                throw Exception("Weather data is null")  // Handle the null case
            }
        } else {
            throw Exception("Failed to fetch weather data: ${response.errorBody()?.string()}")
        }
    }

}
