package com.example.meteora.features.map.view

import DailyWeatherListAdapter
import HourlyWeatherListAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.meteora.R
import com.example.meteora.data.SettingControl
import com.example.meteora.db.local.LocalDataSourceImpl
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.homescreen.viewModel.HomeViewModelFactory
import com.example.meteora.helpers.Constants.CELSIUS_SHARED
import com.example.meteora.helpers.Constants.FAHRENHEIT_SHARED
import com.example.meteora.helpers.Constants.KELVIN_SHARED
import com.example.meteora.model.DailyForecast
import com.example.meteora.model.Forcast
import com.example.meteora.model.HourlyWeatherData
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiClient
import com.example.meteora.network.ApiState
import com.example.meteora.network.RemoteDataSourceImpl
import com.example.meteora.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class WeatherBasedLocationFragment : DialogFragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var settingControl: SettingControl
    private lateinit var progressBar: ProgressBar
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var cloudsTextView: TextView
    private lateinit var dateTimeTextView: TextView
    private lateinit var cityNameTextView: TextView
    private lateinit var exitButton: ImageButton
    private lateinit var lottieAnimationView1: LottieAnimationView

    private lateinit var pastHourlyRecyclerView: RecyclerView
    private lateinit var futureDailyRecyclerView: RecyclerView
    private lateinit var hourlyWeatherAdapter: HourlyWeatherListAdapter
    private lateinit var dailyWeatherAdapter: DailyWeatherListAdapter

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.let { window ->
            window.attributes.windowAnimations = R.raw.dialog
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.setDimAmount(0.8f)

            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = params
        }
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
        settingControl = SettingControl(requireContext())
        progressBar = view.findViewById(R.id.progressBar)
        weatherDescriptionTextView = view.findViewById(R.id.weatherDescriptionTextView)
        temperatureTextView = view.findViewById(R.id.tempTextView)
        humidityTextView = view.findViewById(R.id.humidityTextView)
        windSpeedTextView = view.findViewById(R.id.windSpeedTextView)
        pressureTextView = view.findViewById(R.id.pressureTextView)
        cloudsTextView = view.findViewById(R.id.cloudsTextView)
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView)
        cityNameTextView = view.findViewById(R.id.cityNameTextView)
        exitButton = view.findViewById(R.id.exitButton)

        pastHourlyRecyclerView = view.findViewById(R.id.pastHourlyRecyclerView)
        futureDailyRecyclerView = view.findViewById(R.id.futureDailyRecyclerView)
        lottieAnimationView1 = view.findViewById(R.id.lottieAnimationView);
        hourlyWeatherAdapter = HourlyWeatherListAdapter(settingControl)
        dailyWeatherAdapter = DailyWeatherListAdapter(settingControl)

        pastHourlyRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        futureDailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        pastHourlyRecyclerView.adapter = hourlyWeatherAdapter
        futureDailyRecyclerView.adapter = dailyWeatherAdapter
        exitButton.setOnClickListener {
            dismiss()
        }

        fetchWeatherData()
        return view
    }

    private fun fetchWeatherData() {
        lifecycleScope.launch {
            initializeWeatherData(latitude, longitude)
            observeViewModel()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.weatherData.collect { state ->
                    when (state) {
                        is ApiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                        }
                        is ApiState.Success<*> -> {
                            progressBar.visibility = View.GONE
                            when (val data = state.data) {
                                is Weather -> {
                                    showWeatherViews()
                                    val tempUnit = settingControl.getTemperatureUnit()
                                    val convertedTemp = convertTemperature(data.main.temp, tempUnit).toInt()
                                    temperatureTextView.text = when (tempUnit) {
                                        CELSIUS_SHARED -> "$convertedTemp °C"
                                        FAHRENHEIT_SHARED -> "$convertedTemp °F"
                                        KELVIN_SHARED -> "$convertedTemp °K"
                                        else -> "$convertedTemp °C"
                                    }
                                    humidityTextView.text = "Humidity:${data.main.humidity}%"
                                    pressureTextView.text = "Pressure:${data.main.pressure} hPa"
                                    windSpeedTextView.text = if (settingControl.getWindSpeedUnit() == "metric") {
                                        "Wind Speed:${data.wind.speed} m/s"
                                    } else {
                                        "Wind Speed:${data.wind.speed} mph"
                                    }
                                    cloudsTextView.text = "Clouds:${data.clouds.all} %"
                                    if(data.weather[0].description == "clear sky")
                                    {
                                        lottieAnimationView1.setAnimation(R.raw.clearsky);
                                        lottieAnimationView1.playAnimation();
                                        lottieAnimationView1.loop(true);
                                    }
                                    else if(data.weather[0].description == "few clouds")
                                    {
                                        lottieAnimationView1.setAnimation(R.raw.cold);
                                        lottieAnimationView1.playAnimation();
                                        lottieAnimationView1.loop(true);
                                    }
                                    else if(data.weather[0].description == "scattered clouds")
                                    {
                                        lottieAnimationView1.setAnimation(R.raw.scattled);
                                        lottieAnimationView1.playAnimation();
                                        lottieAnimationView1.loop(true);
                                    }
                                    else if (data.weather[0].description == "broken clouds")
                                    {
                                        lottieAnimationView1.setAnimation(R.raw.scattled);
                                        lottieAnimationView1.playAnimation();
                                        lottieAnimationView1.loop(true);
                                    }
                                    else if(data.weather[0].description == "shower rain")
                                    {
                                        lottieAnimationView1.setAnimation(R.raw.rain);
                                        lottieAnimationView1.playAnimation();
                                        lottieAnimationView1.loop(true);
                                    }
                                    else if(data.weather[0].description == "rain")
                                    {
                                        lottieAnimationView1.setAnimation(R.raw.rain);
                                        lottieAnimationView1.playAnimation();
                                        lottieAnimationView1.loop(true);
                                    }
                                    else
                                    {
                                        lottieAnimationView1.setAnimation(R.raw.clearsky);
                                        lottieAnimationView1.playAnimation();
                                        lottieAnimationView1.loop(true);
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

            launch {
                viewModel.forecastData.collect { state ->
                    when (state) {
                        is ApiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                        }
                        is ApiState.Success<*> -> {
                            progressBar.visibility = View.GONE
                            if (state.data is Forcast) {
                                showWeatherViews()
                                weatherDescriptionTextView.text = state.data.list[0].weather[0].description.capitalize()
                                dateTimeTextView.text = "Date and Time: ${state.data.list[0].dtTxt}"
                                cityNameTextView.text = state.data.city.name

                                // Prepare hourly and daily data for adapters
                                val hourlyData = state.data.list.take(12).map { forecast ->
                                    HourlyWeatherData(
                                        time =forecast.dtTxt.substring(11, 16),
                                        temp = forecast.main.temp,
                                        description = forecast.weather[0].description
                                    )
                                }

                                val dailyData = state.data.list.groupBy { forecast ->
                                    forecast.dtTxt.substring(0, 10)
                                }.values.take(5).map { dayForecasts ->
                                    val firstForecast = dayForecasts[0]
                                    DailyForecast(
                                        date = firstForecast.dtTxt.substring(0, 10),
                                        maxTemp = dayForecasts.maxOf { it.main.tempMax },
                                        minTemp = dayForecasts.minOf { it.main.tempMin }
                                    )
                                }

                                hourlyWeatherAdapter.submitList(hourlyData)
                                dailyWeatherAdapter.submitList(dailyData)
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
    }
    private fun initializeWeatherData(lat: Double, lon: Double) {
        observeViewModel()
        viewModel.fetchCurrentWeather(lat, lon)
        viewModel.fetchForecast(lat, lon)
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
    private fun convertTemperature(tempInCelsius: Double, unit: String): Double {
        return when (unit) {
            CELSIUS_SHARED -> tempInCelsius
            FAHRENHEIT_SHARED -> (tempInCelsius * 9 / 5) + 32
            KELVIN_SHARED -> tempInCelsius + 273.15
            else -> tempInCelsius
        }
    }
}
