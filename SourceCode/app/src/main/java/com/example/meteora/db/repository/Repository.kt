package com.example.meteora.db.repository

import com.example.meteora.model.Weather
import com.example.meteora.network.ApiState
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun fetchCurrentWeather(lat: Double, long: Double):  Flow<ApiState>
}