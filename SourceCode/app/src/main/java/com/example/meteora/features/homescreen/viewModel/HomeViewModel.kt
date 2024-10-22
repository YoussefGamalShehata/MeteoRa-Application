package com.example.meteora.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meteora.db.repository.Repository
import com.example.meteora.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {
    private val _weatherData = MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherData: StateFlow<ApiState> get() = _weatherData

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchCurrentWeather(lat: Double, lon: Double) {
        _weatherData.value = ApiState.Loading // Set loading state
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response : ApiState = repository.fetchCurrentWeather(lat, lon).first()
                _weatherData.value = ApiState.Success(response) // Set success state with weather data
            } catch (e: Exception) {
                _weatherData.value = ApiState.Failure(e.message ?: "An error occurred") // Set failure state with error message
                _error.value = ApiState.Failure(e.message ?: "An error occurred").toString() // Set failure state with error message

            }

        }
    }
}
