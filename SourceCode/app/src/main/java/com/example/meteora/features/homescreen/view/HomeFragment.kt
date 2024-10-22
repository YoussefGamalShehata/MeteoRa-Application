package com.example.meteora.features.homescreen.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.meteora.R
import com.example.meteora.features.homescreen.viewModel.HomeViewModelFactory
import com.example.meteora.network.ApiState
import com.example.meteora.ui.home.HomeViewModel


class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Assuming you have a way to get the repository instance (e.g., from a ViewModelStore or other DI framework)
        val repository = (activity?.application as ).repository // Example to get repository
        val factory = HomeViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        // Now you can observe the weather data and errors
        lifecycleScope.launchWhenStarted {
            viewModel.weatherData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {

                    }
                    is ApiState.Success -> {

                    }
                    is ApiState.Failure -> {

                    }
                }
            }
        }

        return view
    }
}