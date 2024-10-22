package com.example.meteora.db.repository

import com.example.meteora.db.local.LocalDataSource
import com.example.meteora.db.remote.RemoteDataSource
import com.example.meteora.helpers.Constants
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource): Repository {
    override fun fetchCurrentWeather(lat: Double, lon: Double): Flow<ApiState> {
        return flow {
            emit(ApiState.Loading) // Emit loading state
            try {
                // Make API call
                val response: Response<Weather> = remoteDataSource.getCurrentWeather(lat, lon,
                    Constants.UNITS)

                // Check if response is successful and emit appropriate ApiState
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(ApiState.Success(it)) // Emit success with the weather data
                    } ?: emit(ApiState.Failure("Response body is null"))
                } else {
                    emit(ApiState.Failure("Error: ${response.message()}")) // Emit failure with the error message
                }
            } catch (e: Exception) {
                emit(ApiState.Failure(e.message ?: "Unknown error occurred")) // Emit failure with exception message
            }
        }
    }
    //private val localDataSource: LocalDataSource
}