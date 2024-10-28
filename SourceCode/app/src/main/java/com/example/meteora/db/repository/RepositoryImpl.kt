package com.example.meteora.db.repository

import android.util.Log
import com.example.meteora.db.local.LocalDataSource
import com.example.meteora.db.remote.RemoteDataSource
import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiClient
import com.example.meteora.network.RemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : Repository {

    override fun fetchCurrentWeather(lat: Double, lon: Double, units: String,lang : String): Flow<Weather> = flow {
        val response = remoteDataSource.getCurrentWeather(lat, lon, units,lang)

        if (response.isSuccessful) {
            //Log.i("EL JOKES", "fetchCurrentWeather: ${response.body()?.weatherParam?.get(0)?.description} ")
            response.body()?.let { weather ->
                emit(weather)  // Emit the weather data
            } ?: run {
                throw Exception("Weather data is null")  // Handle the null case
            }
        } else {
            throw Exception("Failed to fetch weather data: ${response.errorBody()?.string()}")
        }
    }

    override fun fetchForecast(
        lat: Double,
        long: Double,
        units: String,
        lang: String
    ): Flow<Forcast> = flow {

        val response = remoteDataSource.getForecast(lat, long, units,lang)

        if (response.isSuccessful) {
            response.body()?.let { weather ->
                emit(weather)  // Emit the weather data
            } ?: run {
                throw Exception("Forecast data is null")
            }
        } else {
            throw Exception("Failed to fetch forecast data: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun insertForecast(forecast: Forcast) {
        localDataSource.insertForecast(forecast)
    }

    override suspend fun getAllForecast(): List<Forcast> {
        return localDataSource.getAllForecast()
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        localDataSource.deleteForecast(forecast)
    }

    override suspend fun updateForecast(forecast: Forcast) {
        localDataSource.updateForecast(forecast)
    }

}
