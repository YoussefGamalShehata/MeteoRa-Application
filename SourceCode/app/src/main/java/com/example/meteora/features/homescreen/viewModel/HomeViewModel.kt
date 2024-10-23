package com.example.meteora.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meteora.db.repository.Repository
import com.example.meteora.helpers.Constants
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
        _weatherData.value = ApiState.Loading  // Set loading state
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch the weather data from the repository
                val weather = repository.fetchCurrentWeather(lat, lon, Constants.UNITS).first()

                // Set success state with weather data
                _weatherData.value = ApiState.Success(weather)
            } catch (e: Exception) {
                // Set failure state with error message
                _weatherData.value = ApiState.Failure(e.message ?: "An error occurred")
                _error.value = e.message  // Update error value for UI
            }
        }
    }
}
