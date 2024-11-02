package com.example.meteora.features.favorites.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meteora.model.Forcast
import com.example.meteora.db.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: Repository) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Forcast>>(emptyList())
    val favorites: StateFlow<List<Forcast>> get() = _favorites

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = repository.getAllForecast()
        }
    }

    fun addFavorite(forecast: Forcast) {
        viewModelScope.launch {
            if (!isFavorite(forecast)) {
                repository.insertForecast(forecast)
                loadFavorites()
            } else {
                Log.i("None", "City Already Exist in Favorites ")
            }
        }
    }

    fun removeFavorite(forecast: Forcast) {
        viewModelScope.launch {
            repository.deleteForecast(forecast)
            loadFavorites() // Reload favorites after removal
        }
    }

    private fun isFavorite(forecast: Forcast): Boolean {
        return _favorites.value.any { it.city.name == forecast.city.name } // Check by city name
    }

    fun updateFavoriteStatus(forecast: Forcast) {
        if (isFavorite(forecast)) {
            removeFavorite(forecast)
        } else {
            addFavorite(forecast)
        }
    }

    fun getFavoriteByCityName(city: String): Forcast? {
        return _favorites.value.find { it.city.name == city }
    }
}
