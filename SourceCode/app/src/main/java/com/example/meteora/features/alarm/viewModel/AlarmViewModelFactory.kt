package com.example.meteora.features.alarm.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.meteora.ui.home.HomeViewModel

class AlarmViewModelFactory(
    private val context: Context,
    private val homeViewModel: HomeViewModel // Add HomeViewModel as a parameter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            return AlarmViewModel(context, homeViewModel) as T // Pass both parameters
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
