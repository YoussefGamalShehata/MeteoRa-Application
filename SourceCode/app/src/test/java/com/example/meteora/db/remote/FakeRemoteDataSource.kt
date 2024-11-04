package com.example.meteora.db.remote

import com.example.meteora.db.local.LocalDataSource
import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

class FakeRemoteDataSource : RemoteDataSource {

    var weather: Weather? = null
    var forecast: Forcast? = null
    var shouldReturnError: Boolean = false

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Response<Weather> {
        return if (shouldReturnError) {
            Response.error(
                500,
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    "{\"error\":\"Failed to fetch current weather\"}"
                )
            )
        } else {
            Response.success(weather)
        }
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Response<Forcast> {
        return if (shouldReturnError) {
            Response.error(
                500,
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    "{\"error\":\"Failed to fetch forecast\"}"
                )
            )
        } else {
            Response.success(forecast)
        }
    }
}
