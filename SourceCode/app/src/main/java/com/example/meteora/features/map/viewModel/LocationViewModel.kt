package com.example.meteora.features.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint

class LocationViewModel : ViewModel() {

    private val _selectedLocation = MutableStateFlow<GeoPoint?>(null)
    val selectedLocation: StateFlow<GeoPoint?> get() = _selectedLocation

    fun updateSelectedLocation(location: GeoPoint) {
        _selectedLocation.value = location
    }
}
