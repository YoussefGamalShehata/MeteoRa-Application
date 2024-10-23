import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.meteora.R
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.homescreen.viewModel.HomeViewModelFactory
import com.example.meteora.network.ApiClient
import com.example.meteora.network.ApiState
import com.example.meteora.network.RemoteDataSourceImpl
import com.example.meteora.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: RepositoryImpl
//    private lateinit var progressBar: ProgressBar
//    private lateinit var errorImageView: ImageView
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var temperatureTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
     //   progressBar = view.findViewById(R.id.progressBar)
     //   errorImageView = view.findViewById(R.id.errorImageView)
        weatherDescriptionTextView = view.findViewById(R.id.weatherDescriptionTextView)
        temperatureTextView = view.findViewById(R.id.tempTextView)

        // Initialize the ViewModel
        val factory = HomeViewModelFactory(RepositoryImpl(RemoteDataSourceImpl.getInstance(ApiClient.retrofit)))
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // Observe LiveData or StateFlow from the ViewModel
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
                    //    progressBar.visibility = View.VISIBLE
                    //    errorImageView.visibility = View.GONE
                        weatherDescriptionTextView.visibility = View.GONE
                        temperatureTextView.visibility = View.GONE
                    }
                    is ApiState.Success -> {
                   //     progressBar.visibility = View.GONE
                    //    errorImageView.visibility = View.GONE
                        weatherDescriptionTextView.visibility = View.VISIBLE
                        temperatureTextView.visibility = View.VISIBLE

                        // Update UI with new data
                        weatherDescriptionTextView.text = state.data.main.humidity.toString()
                        temperatureTextView.text = "${state.data.main.temp}°"
                    }
                    is ApiState.Failure -> {
                   //     progressBar.visibility = View.GONE
                   //     errorImageView.visibility = View.VISIBLE
                        weatherDescriptionTextView.visibility = View.GONE
                        temperatureTextView.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.fetchCurrentWeather(lat = 37.7749, lon = -122.4194) // Example coordinates
    }
}
