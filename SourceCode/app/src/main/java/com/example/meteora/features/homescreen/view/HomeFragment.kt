import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.meteora.R
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.homescreen.viewModel.HomeViewModelFactory
import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiClient
import com.example.meteora.network.ApiState
import com.example.meteora.network.RemoteDataSourceImpl
import com.example.meteora.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var progressBar: ProgressBar
//    private lateinit var errorImageView: ImageView
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var cloudsTextView: TextView
    private lateinit var dateTimeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        progressBar = view.findViewById(R.id.progressBar)
     //   errorImageView = view.findViewById(R.id.errorImageView)
        weatherDescriptionTextView = view.findViewById(R.id.weatherDescriptionTextView)
        temperatureTextView = view.findViewById(R.id.tempTextView)
        humidityTextView = view.findViewById(R.id.humidityTextView)
        windSpeedTextView = view.findViewById(R.id.windSpeedTextView)
        pressureTextView = view.findViewById(R.id.pressureTextView)
        cloudsTextView = view.findViewById(R.id.cloudsTextView)
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView)


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
            viewModel.weatherData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {

                    }
                    is ApiState.Success<*> -> {
                        when (val data = state.data) {
                            is Weather -> {
                                // Handle ForecastResponse data
                                showWeatherViews()
                             //   weatherDescriptionTextView.text = data.list[0].weather[0].description.capitalize()
                                temperatureTextView.text = "${data.main.temp} °C"
                                humidityTextView.text = "${data.main.humidity}%"
                                windSpeedTextView.text = "${data.wind.speed} m/s"
                                pressureTextView.text = "${data.main.pressure} hPa"
                                cloudsTextView.text = "${data.clouds.all} %"

                            }
                            is Forcast -> {
                                // Handle WeatherResponse data
                                showWeatherViews()
                           //     weatherDescriptionTextView.text = data.list[0].weather[0].description.capitalize()
                                temperatureTextView.text = "${data.city.name}"
                                humidityTextView.text = "${data.city.country}"

                            }
                        }
                    }

                    is ApiState.Failure -> {
                        hideWeatherViews()
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
         viewModel.fetchCurrentWeather(38.7946, 106.5348, "metric", "ar")
       // viewModel.fetchForecast(38.7946, 106.5348, "metric", "en")
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
