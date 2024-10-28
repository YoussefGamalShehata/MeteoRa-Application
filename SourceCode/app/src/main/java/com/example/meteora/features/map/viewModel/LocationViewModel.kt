package com.example.meteora.features.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.meteora.db.local.LocalDataSourceImpl
import com.example.meteora.db.repository.Repository
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.model.Forcast
import com.example.meteora.network.ApiState
import com.example.meteora.network.RemoteDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint

class LocationViewModel(private val repository: Repository) : ViewModel() {

    private val _selectedLocation = MutableStateFlow<GeoPoint?>(null)
    val selectedLocation: StateFlow<GeoPoint?> get() = _selectedLocation

    private val _forecastData = MutableStateFlow<ApiState<Forcast>>(ApiState.Loading)
    val forecastData: StateFlow<ApiState<Forcast>> get() = _forecastData

    fun updateSelectedLocation(location: GeoPoint) {
        _selectedLocation.value = location
        fetchForecast(location.latitude, location.longitude)
    }

    private fun fetchForecast(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _forecastData.value = ApiState.Loading
                val forecast = repository.fetchForecast(lat, lon, "metric", "ar").first() // Assuming fetchForecast is defined in the repository
                _forecastData.value = ApiState.Success(forecast)
            } catch (e: Exception) {
                _forecastData.value = ApiState.Failure(e.message ?: "An error occurred")
            }
        }
    }
}

