import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.db.local.LocalDataSourceImpl
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.homescreen.viewModel.HomeViewModelFactory
import com.example.meteora.features.map.view.MapFragment
import com.example.meteora.model.DailyForecast
import com.example.meteora.model.Forcast
import com.example.meteora.model.HourlyWeatherData
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiClient
import com.example.meteora.network.ApiState
import com.example.meteora.network.RemoteDataSourceImpl
import com.example.meteora.ui.home.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var progressBar: ProgressBar
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var cloudsTextView: TextView
    private lateinit var dateTimeTextView: TextView
    private lateinit var cityNameTextView: TextView
    private lateinit var chooseLocationButton: Button

    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var dailyRecyclerView: RecyclerView
    private lateinit var hourlyWeatherAdapter: HourlyWeatherListAdapter
    private lateinit var dailyWeatherAdapter: DailyWeatherListAdapter

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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
        chooseLocationButton = view.findViewById(R.id.chooseLocationButton)

        // Initialize RecyclerViews and Adapters
        hourlyRecyclerView = view.findViewById(R.id.hourlyRecyclerView)
        dailyRecyclerView = view.findViewById(R.id.dailyRecyclerView)

        hourlyWeatherAdapter = HourlyWeatherListAdapter()
        dailyWeatherAdapter = DailyWeatherListAdapter()

        hourlyRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        dailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        hourlyRecyclerView.adapter = hourlyWeatherAdapter
        dailyRecyclerView.adapter = dailyWeatherAdapter

        val factory = HomeViewModelFactory(RepositoryImpl(RemoteDataSourceImpl.getInstance(ApiClient.retrofit), LocalDataSourceImpl(requireContext())), requireContext())
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        chooseLocationButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MapFragment())
                .addToBackStack(null)
                .commit()
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        } else {
            fetchLocationAndInitializeWeatherData()
        }

        return view
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndInitializeWeatherData()
            } else {
                Toast.makeText(requireContext(), "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocationAndInitializeWeatherData() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                initializeWeatherData(location.latitude, location.longitude)
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeWeatherData(lat: Double, lon: Double) {
        observeViewModel()
        viewModel.fetchCurrentWeather(lat, lon)
        viewModel.fetchForecast(lat, lon)
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
                                    temperatureTextView.text = "${data.main.temp} °C"
                                    humidityTextView.text = "Humidity: ${data.main.humidity}%"
                                    windSpeedTextView.text = "Wind Speed: ${data.wind.speed} m/s"
                                    pressureTextView.text = "Pressure: ${data.main.pressure} hPa"
                                    cloudsTextView.text = "Cloudiness: ${data.clouds.all} %"
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
                                        time = forecast.dtTxt,
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

    private fun hideWeatherViews() {
        weatherDescriptionTextView.visibility = View.GONE
        temperatureTextView.visibility = View.GONE
        humidityTextView.visibility = View.GONE
        windSpeedTextView.visibility = View.GONE
        pressureTextView.visibility = View.GONE
        cloudsTextView.visibility = View.GONE
        dateTimeTextView.visibility = View.GONE
        cityNameTextView.visibility = View.GONE
        chooseLocationButton.visibility = View.GONE
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
        chooseLocationButton.visibility = View.VISIBLE
    }
}
