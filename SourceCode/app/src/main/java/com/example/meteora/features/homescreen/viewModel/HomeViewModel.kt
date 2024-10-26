package com.example.meteora.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meteora.db.repository.Repository
import com.example.meteora.helpers.Constants
import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _weatherData = MutableStateFlow<ApiState<Weather>>(ApiState.Loading)
    val weatherData: StateFlow<ApiState<Weather>> get() = _weatherData

    private val _weathererror = MutableStateFlow<String?>(null)
    val weathererror: StateFlow<String?> get() = _weathererror

    private val _forcastData = MutableStateFlow<ApiState<Forcast>>(ApiState.Loading)
    val forcastData: StateFlow<ApiState<Forcast>> get() = _forcastData

    private val _forcasterror = MutableStateFlow<String?>(null)
    val forcasterror: StateFlow<String?> get() = _forcasterror

    fun fetchCurrentWeather(lat: Double, lon: Double, units: String, lang: String) {
        _weatherData.value = ApiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weather = repository.fetchCurrentWeather(lat, lon, units, lang).first()
                _weatherData.value = ApiState.Success(weather)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun fetchForecast(lat: Double, lon: Double, units: String, lang: String) {
        _forcastData.value = ApiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val forecast = repository.fetchForecast(lat, lon, units, lang).first()

                _forcastData.value = ApiState.Success(forecast)
            } catch (e: Exception) {
                handleForecastError(e)
            }
        }
    }

    private fun handleError(exception: Exception) {
        _weatherData.value = ApiState.Failure(exception.message ?: "An error occurred")
        _weathererror.value = exception.message
    }

    private fun handleForecastError(exception: Exception) {
        Log.i("fff", "handleError: kekekeke ")
        _forcastData.value = ApiState.Failure(exception.message ?: "An error occurred")
        _forcasterror.value = exception.message
    }
}
