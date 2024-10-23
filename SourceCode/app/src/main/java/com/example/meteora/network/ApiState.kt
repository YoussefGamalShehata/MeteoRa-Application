package com.example.meteora.network

import com.example.meteora.model.Weather

//sealed class ApiState {
//    class Success(val data: Weather) : ApiState()
//    class Failure(val message: String) : ApiState()
//    object Loading : ApiState()
//}
sealed class ApiState<out T> {
    object Loading : ApiState<Nothing>()
    data class Success<out T>(val data: T) : ApiState<T>()
    data class Failure(val message: String) : ApiState<Nothing>()
}