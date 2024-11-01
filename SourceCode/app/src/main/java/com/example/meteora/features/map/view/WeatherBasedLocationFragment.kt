package com.example.meteora.features.map.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.db.local.LocalDataSourceImpl
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.homescreen.viewModel.HomeViewModelFactory
import com.example.meteora.model.DailyForecast
import com.example.meteora.model.Forcast
import com.example.meteora.model.HourlyWeatherData
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

    private lateinit var pastHourlyRecyclerView: RecyclerView
    private lateinit var futureDailyRecyclerView: RecyclerView

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            latitude = it.getDouble("latitude", 0.0)
            longitude = it.getDouble("longitude", 0.0)
        }

        val factory = HomeViewModelFactory(
            RepositoryImpl(
                RemoteDataSourceImpl.getInstance(ApiClient.retrofit),
                LocalDataSourceImpl(requireContext())
            ), requireContext()
        )
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_based_location, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        weatherDescriptionTextView = view.findViewById(R.id.weatherDescriptionTextView)
        temperatureTextView = view.findViewById(R.id.tempTextView)
        humidityTextView = view.findViewById(R.id.humidityTextView)
        windSpeedTextView = view.findViewById(R.id.windSpeedTextView)
        pressureTextView = view.findViewById(R.id.pressureTextView)
        cloudsTextView = view.findViewById(R.id.cloudsTextView)
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView)
        cityNameTextView = view.findViewById(R.id.cityNameTextView)

        pastHourlyRecyclerView = view.findViewById(R.id.pastHourlyRecyclerView)
        futureDailyRecyclerView = view.findViewById(R.id.futureDailyRecyclerView)

        pastHourlyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        futureDailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchWeatherData()

        return view
    }

    private fun fetchWeatherData() {
        lifecycleScope.launch {
            viewModel.fetchForecast(latitude, longitude)
            observeViewModel()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.forecastData.collect { state ->
                when (state) {
                    is ApiState.Loading -> progressBar.visibility = View.VISIBLE
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
                                cityNameTextView.text = data.city.name

                                // Prepare hourly and daily data for the adapters
                                val hourlyData = data.list.take(12).map { forecast ->
                                    HourlyWeatherData(
                                        time = forecast.dtTxt,
                                        temp = forecast.main.temp,
                                        description = forecast.weather[0].description
                                    )
                                }

                                val dailyData = data.list.groupBy { forecast ->
                                    forecast.dtTxt.substring(0, 10)
                                }.values.take(5).map { dayForecasts ->
                                    val firstForecast = dayForecasts[0]
                                    DailyForecast(
                                        date = firstForecast.dtTxt.substring(0, 10),
                                        maxTemp = dayForecasts.maxOf { it.main.tempMax },
                                        minTemp = dayForecasts.minOf { it.main.tempMin }
                                    )
                                }

                                // Submit data to adapters
                                pastHourlyRecyclerView.adapter = PastHourlyWeatherAdapter().apply {
                                    submitList(hourlyData)
                                }
                                futureDailyRecyclerView.adapter = FutureDailyWeatherAdapter().apply {
                                    submitList(dailyData)
                                }
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
}
