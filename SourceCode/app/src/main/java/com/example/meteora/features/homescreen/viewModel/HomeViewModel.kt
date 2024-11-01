package com.example.meteora.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meteora.data.SettingControl
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

class HomeViewModel(private val repository: Repository, context: Context) : ViewModel() {

    private val _weatherData = MutableStateFlow<ApiState<Weather>>(ApiState.Loading)
    val weatherData: StateFlow<ApiState<Weather>> get() = _weatherData

    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> get() = _weatherError

    private val _forecastData = MutableStateFlow<ApiState<Forcast>>(ApiState.Loading)
    val forecastData: StateFlow<ApiState<Forcast>> get() = _forecastData

    private val _forecastError = MutableStateFlow<String?>(null)
    val forecastError: StateFlow<String?> get() = _forecastError

    private val settingControl = SettingControl(context)

    // Settings state flows
    private val _temperatureUnit = MutableStateFlow(settingControl.getTemperatureUnit())
    private val _windSpeedUnit = MutableStateFlow(settingControl.getWindSpeedUnit())
    private val _language = MutableStateFlow(settingControl.getLanguage())

    // Expose settings as StateFlows
    val temperatureUnit: StateFlow<String> get() = _temperatureUnit
    val windSpeedUnit: StateFlow<String> get() = _windSpeedUnit
    val language: StateFlow<String> get() = _language

    fun updateSettings(lat: Double, lon: Double) {
        _temperatureUnit.value = settingControl.getTemperatureUnit()
        _windSpeedUnit.value = settingControl.getWindSpeedUnit()
        _language.value = settingControl.getLanguage()

        // Optionally, re-fetch weather and forecast data when settings change
        fetchCurrentWeather(lat, lon)
        fetchForecast(lat, lon)
    }

    fun fetchCurrentWeather(lat: Double, lon: Double) {
        _weatherData.value = ApiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weather = repository.fetchCurrentWeather(lat, lon, _windSpeedUnit.value, _language.value).first()
                _weatherData.value = ApiState.Success(weather)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun fetchForecast(lat: Double, lon: Double) {
        _forecastData.value = ApiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val forecast = repository.fetchForecast(lat, lon, _windSpeedUnit.value, _language.value).first()
                _forecastData.value = ApiState.Success(forecast)
            } catch (e: Exception) {
                handleForecastError(e)
            }
        }
    }

    private fun handleError(exception: Exception) {
        _weatherData.value = ApiState.Failure(exception.message ?: "An error occurred")
        _weatherError.value = exception.message
    }

    private fun handleForecastError(exception: Exception) {
        Log.i("HomeViewModel", "handleForecastError: ${exception.message}")
        _forecastData.value = ApiState.Failure(exception.message ?: "An error occurred")
        _forecastError.value = exception.message
    }
}
