package com.example.meteora.features.detailsBasedLocation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.meteora.R
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.homescreen.viewModel.HomeViewModelFactory
import com.example.meteora.model.Forcast
import com.example.meteora.network.ApiClient
import com.example.meteora.network.ApiState
import com.example.meteora.network.RemoteDataSourceImpl
import com.example.meteora.ui.home.HomeViewModel
import kotlinx.coroutines.launch


class WeatherBasedLocationFragment : DialogFragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var cloudsTextView: TextView
    private lateinit var dateTimeTextView: TextView
    private lateinit var cityNameTextView: TextView

    // Pass latitude and longitude as arguments
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve latitude and longitude from arguments
        arguments?.let {
            latitude = it.getDouble("latitude", 0.0)
            longitude = it.getDouble("longitude", 0.0)
        }

        // Initialize ViewModel
        val factory = HomeViewModelFactory(RepositoryImpl(RemoteDataSourceImpl.getInstance(ApiClient.retrofit)))
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_based_location, container, false)

        // Initialize views
        progressBar = view.findViewById(R.id.progressBar)
        weatherDescriptionTextView = view.findViewById(R.id.weatherDescriptionTextView)
        temperatureTextView = view.findViewById(R.id.tempTextView)
        humidityTextView = view.findViewById(R.id.humidityTextView)
        windSpeedTextView = view.findViewById(R.id.windSpeedTextView)
        pressureTextView = view.findViewById(R.id.pressureTextView)
        cloudsTextView = view.findViewById(R.id.cloudsTextView)
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView)
        cityNameTextView = view.findViewById(R.id.cityNameTextView)

        // Fetch weather data
        fetchWeatherData()

        return view
    }

    private fun fetchWeatherData() {
        lifecycleScope.launch {
            viewModel.fetchForecast(latitude, longitude, "metric", "ar") // Replace with actual units and language if needed
            observeViewModel()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.forcastData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is ApiState.Success<*> -> {
                        progressBar.visibility = View.GONE
                        when (val data = state.data) {
                            is Forcast -> {
                                showWeatherViews()
                                weatherDescriptionTextView.text = data.list[0].weather[0].description.capitalize()
                                temperatureTextView.text = "${data.list[0].main.temp} °C"
                                humidityTextView.text = "Humidity: ${data.list[0].main.humidity} %"
                                windSpeedTextView.text = "Wind Speed: ${data.list[0].wind.speed} m/s"
                                pressureTextView.text = "Pressure: ${data.list[0].main.pressure} hPa"
                                cloudsTextView.text = "Cloudiness: ${data.list[0].clouds.all} %"
                                dateTimeTextView.text = "Date and Time: ${data.list[0].dtTxt}"
                                cityNameTextView.text = data.city.name // Adjust based on your data model
                            }
                        }
                    }
                    is ApiState.Failure -> {
                        progressBar.visibility = View.GONE
                        hideWeatherViews()
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun hideWeatherViews() {
        weatherDescriptionTextView.visibility = View.GONE
        temperatureTextView.visibility = View.GONE
        humidityTextView.visibility = View.GONE
        windSpeedTextView.visibility = View.GONE
        pressureTextView.visibility = View.GONE
        cloudsTextView.visibility = View.GONE
        dateTimeTextView.visibility = View.GONE
        cityNameTextView.visibility = View.GONE
    }

    private fun showWeatherViews() {
        weatherDescriptionTextView.visibility = View.VISIBLE
        temperatureTextView.visibility = View.VISIBLE
        humidityTextView.visibility = View.VISIBLE
        windSpeedTextView.visibility = View.VISIBLE
        pressureTextView.visibility = View.VISIBLE
        cloudsTextView.visibility = View.VISIBLE
        dateTimeTextView.visibility = View.VISIBLE
        cityNameTextView.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(latitude: Double, longitude: Double): WeatherBasedLocationFragment {
            val fragment = WeatherBasedLocationFragment()
            val args = Bundle()
            args.putDouble("latitude", latitude)
            args.putDouble("longitude", longitude)
            fragment.arguments = args
            return fragment
        }
    }

}