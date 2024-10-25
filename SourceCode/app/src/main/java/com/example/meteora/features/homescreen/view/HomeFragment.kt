import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.homescreen.viewModel.HomeViewModelFactory
import com.example.meteora.model.DailyForecast
import com.example.meteora.model.Forcast
import com.example.meteora.model.HourlyWeatherData
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiClient
import com.example.meteora.network.ApiState
import com.example.meteora.network.RemoteDataSourceImpl
import com.example.meteora.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var cloudsTextView: TextView
    private lateinit var dateTimeTextView: TextView

    // RecyclerView and Adapters for hourly and daily forecasts
    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var dailyRecyclerView: RecyclerView
    private lateinit var hourlyWeatherAdapter: HourlyWeatherListAdapter
    private lateinit var dailyWeatherAdapter: DailyWeatherListAdapter

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

        // Initialize RecyclerViews and Adapters
        hourlyRecyclerView = view.findViewById(R.id.hourlyRecyclerView)
        dailyRecyclerView = view.findViewById(R.id.dailyRecyclerView)

        hourlyWeatherAdapter = HourlyWeatherListAdapter()
        dailyWeatherAdapter = DailyWeatherListAdapter()

        hourlyRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        dailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        hourlyRecyclerView.adapter = hourlyWeatherAdapter
        dailyRecyclerView.adapter = dailyWeatherAdapter

        // Initialize the ViewModel
        val factory = HomeViewModelFactory(RepositoryImpl(RemoteDataSourceImpl.getInstance(ApiClient.retrofit)))
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // Observe StateFlow from the ViewModel
        observeViewModel()

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        // Collect weather data state
        lifecycleScope.launch {
            viewModel.forcastData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        // Show progress bar while loading data
                        progressBar.visibility = View.VISIBLE
                    }
                    is ApiState.Success<*> -> {
                        progressBar.visibility = View.GONE
                        when (val data = state.data) {
                            is Weather -> {
                                showWeatherViews()
                                temperatureTextView.text = "${data.main.temp} °C"
                                humidityTextView.text = "${data.main.humidity}%"
                                windSpeedTextView.text = "${data.wind.speed} m/s"
                                pressureTextView.text = "${data.main.pressure} hPa"
                                cloudsTextView.text = "${data.clouds.all} %"
                            }
                            is Forcast -> {
                                showWeatherViews()
                                weatherDescriptionTextView.text = data.list[0].weather[0].description.capitalize()
                                temperatureTextView.text = "${data.list[0].main.temp} °C"
                                humidityTextView.text = "Humidity: ${data.list[0].main.humidity} %"
                                windSpeedTextView.text = "Wind Speed: ${data.list[0].wind.speed} m/s"
                                pressureTextView.text = "Pressure: ${data.list[0].main.pressure} hPa"
                                cloudsTextView.text = "Cloudiness: ${data.list[0].clouds.all} %"
                                dateTimeTextView.text = "Date and Time: ${data.list[0].dtTxt}"

                                // Submit lists to adapters
                                val hourlyData = data.list.take(12).map { forecast ->
                                    // Map the ForecastInfo to HourlyWeatherData if needed
                                    HourlyWeatherData(
                                        time = forecast.dtTxt, // Assuming dtTxt is a valid time string
                                        temp = forecast.main.temp ,
                                        description = forecast.weather[0].description
                                        // Add other properties as needed
                                    )
                                }

                                val dailyData = data.list.take(7).map { forecast ->
                                    // Map the ForecastInfo to DailyWeatherData if needed
                                    DailyForecast(
                                        date = forecast.dtTxt, // Assuming dtTxt is a valid date string
                                        maxTemp = forecast.main.tempMax,
                                        minTemp = forecast.main.tempMin
                                        // Add other properties as needed
                                    )
                                }

                                hourlyWeatherAdapter.submitList(hourlyData)
                                dailyWeatherAdapter.submitList(dailyData)
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

        // Fetch forecast data
        viewModel.fetchForecast(38.7946, 109.5348, "metric", "ar")
    }

    private fun hideWeatherViews() {
        weatherDescriptionTextView.visibility = View.GONE
        temperatureTextView.visibility = View.GONE
        humidityTextView.visibility = View.GONE
        windSpeedTextView.visibility = View.GONE
        pressureTextView.visibility = View.GONE
        cloudsTextView.visibility = View.GONE
        dateTimeTextView.visibility = View.GONE
    }

    private fun showWeatherViews() {
        weatherDescriptionTextView.visibility = View.VISIBLE
        temperatureTextView.visibility = View.VISIBLE
        humidityTextView.visibility = View.VISIBLE
        windSpeedTextView.visibility = View.VISIBLE
        pressureTextView.visibility = View.VISIBLE
        cloudsTextView.visibility = View.VISIBLE
        dateTimeTextView.visibility = View.VISIBLE
    }
}
