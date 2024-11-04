import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.meteora.R
import com.example.meteora.data.SettingControl
import com.example.meteora.db.local.LocalDataSourceImpl
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.homescreen.viewModel.HomeViewModelFactory
import com.example.meteora.features.map.view.MapFragment
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var settingControl: SettingControl
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var progressBar: ProgressBar
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var lottieAnimationView1: LottieAnimationView
    private lateinit var dateTimeTextView: TextView
    private lateinit var cityNameTextView: TextView
    private lateinit var  tv_humidity: TextView
    private lateinit var  tv_pressure: TextView
    private lateinit var  tv_wind: TextView
    private lateinit var  tv_cloud: TextView

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
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        lottieAnimationView1 = view.findViewById(R.id.lottieAnimationView1);
        lottieAnimationView.setAnimation(R.raw.mapchoose);
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        lottieAnimationView.playAnimation();
        settingControl = SettingControl(requireContext())
        progressBar = view.findViewById(R.id.progressBar)
        weatherDescriptionTextView = view.findViewById(R.id.weatherDescriptionTextView)
           temperatureTextView = view.findViewById(R.id.tempTextView)
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView)
        cityNameTextView = view.findViewById(R.id.cityNameTextView)
        tv_humidity = view.findViewById(R.id.tv_humidity_value)
        tv_pressure = view.findViewById(R.id.tv_pressure_value)
        tv_wind = view.findViewById(R.id.tv_wind_value)
        tv_cloud = view.findViewById(R.id.tv_cloud_value)

        // Initialize RecyclerViews and Adapters
        hourlyRecyclerView = view.findViewById(R.id.hourlyRecyclerView)
        dailyRecyclerView = view.findViewById(R.id.dailyRecyclerView)

        hourlyWeatherAdapter = HourlyWeatherListAdapter(settingControl)
        dailyWeatherAdapter = DailyWeatherListAdapter(settingControl)

        hourlyRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        dailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        hourlyRecyclerView.adapter = hourlyWeatherAdapter
        dailyRecyclerView.adapter = dailyWeatherAdapter

        val factory = HomeViewModelFactory(RepositoryImpl(RemoteDataSourceImpl.getInstance(ApiClient.retrofit), LocalDataSourceImpl(requireContext())), requireContext())
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        lottieAnimationView.setOnClickListener {
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
                                    val tempUnit = settingControl.getTemperatureUnit()
                                    val convertedTemp = convertTemperature(data.main.temp, tempUnit).toInt()
                                    temperatureTextView.text = when (tempUnit) {
                                        CELSIUS_SHARED -> "$convertedTemp °C"
                                        FAHRENHEIT_SHARED -> "$convertedTemp °F"
                                        KELVIN_SHARED -> "$convertedTemp °K"
                                        else -> "$convertedTemp °C"
                                    }
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

                                    if (settingControl.getLanguage() == "ar") {
                                        val arabicLocale = Locale("ar", "EG")  // Locale for Arabic (Egypt)

                                        val humidity = NumberFormat.getInstance(arabicLocale).format(data.main.humidity)
                                        val pressure = NumberFormat.getInstance(arabicLocale).format(data.main.pressure)

                                        tv_humidity.text = "$humidity %"
                                        tv_pressure.text = "$pressure  هيكتو"  // You could replace "hPa" with Arabic if needed
                                    } else {
                                        // Use default English display
                                        tv_humidity.text = "${data.main.humidity} %"
                                        tv_pressure.text = "${data.main.pressure} hPa"
                                    }
                                    tv_wind.text = if (settingControl.getWindSpeedUnit() == "metric") {
                                        if (settingControl.getLanguage() == "ar") {
                                            val arabicLocale = Locale("ar", "EG")  // Locale for Arabic (Egypt)
                                            val windSpeed = NumberFormat.getInstance(arabicLocale).format(data.wind.speed)
                                            "$windSpeed م/ث"
                                        }
                                        else{
                                            "${data.wind.speed} mph"
                                        }
                                    } else {
                                        if (settingControl.getLanguage() == "ar") {
                                            val arabicLocale = Locale("ar", "EG")  // Locale for Arabic (Egypt)
                                            val windSpeed = NumberFormat.getInstance(arabicLocale).format(data.wind.speed)
                                            "$windSpeed ميل/ساعة"
                                        }
                                        else{
                                            "${data.wind.speed} mph"
                                        }
                                    }
                                    if (settingControl.getLanguage() == "ar") {
                                        val arabicLocale = Locale("ar", "eg")  // Locale for Arabic (Egypt)
                                        val cloudPercentage = NumberFormat.getInstance(arabicLocale).format(data.clouds.all)
                                        tv_cloud.text = "$cloudPercentage %"
                                    }
                                    else{
                                        tv_cloud.text = "${data.clouds.all} %"
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
                                dateTimeTextView.text = "Date : ${state.data.list[0].dtTxt.substring(0, 10)}"
                                cityNameTextView.text = state.data.city.name


                                val hourlyData = state.data.list.take(12).map { forecast ->
                                    val temp = convertTemperature(forecast.main.temp, settingControl.getTemperatureUnit())

                                    HourlyWeatherData(
                                        time =forecast.dtTxt.substring(11, 16),
                                        temp,
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
        tv_humidity.visibility = View.GONE
        tv_pressure.visibility = View.GONE
        tv_wind.visibility = View.GONE
        tv_cloud.visibility = View.GONE
        dateTimeTextView.visibility = View.GONE
        cityNameTextView.visibility = View.GONE
        lottieAnimationView.visibility = View.GONE
    }

    private fun showWeatherViews() {
        weatherDescriptionTextView.visibility = View.VISIBLE
        temperatureTextView.visibility = View.VISIBLE
        tv_humidity.visibility = View.VISIBLE
        tv_pressure.visibility = View.VISIBLE
        tv_wind.visibility = View.VISIBLE
        tv_cloud.visibility = View.VISIBLE
        dateTimeTextView.visibility = View.VISIBLE
        cityNameTextView.visibility = View.VISIBLE
        lottieAnimationView.visibility = View.VISIBLE
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
