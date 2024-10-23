package com.example.meteora.network

import com.example.meteora.model.Weather

sealed class ApiState {
    class Success(val data: Weather) : ApiState()
    class Failure(val message: String) : ApiState()
    object Loading : ApiState()
}