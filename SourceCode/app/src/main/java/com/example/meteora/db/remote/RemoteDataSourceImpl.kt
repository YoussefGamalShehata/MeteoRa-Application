package com.example.meteora.network

import com.example.meteora.db.remote.RemoteDataSource
import com.example.meteora.helpers.Constants
import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import retrofit2.Response

class RemoteDataSourceImpl private constructor(private val apiService: ApiService) : RemoteDataSource {
    override suspend fun getCurrentWeather(lat: Double, lon: Double, units: String,lang : String): Response<Weather> {
        return apiService.getCurrentWeather(lat, lon, Constants.API_KEY, units,lang)
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Response<Forcast> {
        return apiService.getForecast(lat, lon, Constants.API_KEY, units,lang)
    }

    companion object {
        @Volatile
        private var instance: RemoteDataSourceImpl? = null

        fun getInstance(apiService: ApiService): RemoteDataSourceImpl {
            return instance ?: synchronized(this) {
                instance ?: RemoteDataSourceImpl(apiService).also { instance = it }
            }
        }
    }
}
