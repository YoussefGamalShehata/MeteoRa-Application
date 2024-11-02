import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.data.SettingControl
import com.example.meteora.helpers.Constants.CELSIUS_SHARED
import com.example.meteora.helpers.Constants.FAHRENHEIT_SHARED
import com.example.meteora.helpers.Constants.KELVIN_SHARED
import com.example.meteora.helpers.Constants.TEMPERATURE_FORMAT
import com.example.meteora.model.HourlyWeatherData

class HourlyWeatherListAdapter(
    private val settingControl: SettingControl
) : ListAdapter<HourlyWeatherData, HourlyWeatherListAdapter.HourlyWeatherViewHolder>(HourlyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hour_home, parent, false)
        return HourlyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val hourlyWeather = getItem(position)
        // Get temperature unit and convert temperature accordingly
        val unit = settingControl.getTemperatureUnit()
        val convertedTemp = convertTemperature(hourlyWeather.temp, unit)
        holder.bind(hourlyWeather, convertedTemp, getUnitSymbol(unit))
    }

    class HourlyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeTextView: TextView = itemView.findViewById(R.id.time_hour)
        private val tempTextView: TextView = itemView.findViewById(R.id.tv_degree_day_hour)
        private val weatherIconImageView: ImageView = itemView.findViewById(R.id.imv_weather_hour)

        fun bind(hourlyWeather: HourlyWeatherData, temp: Double, unitSymbol: String) {
            timeTextView.text = hourlyWeather.time
            tempTextView.text = String.format(TEMPERATURE_FORMAT, temp, unitSymbol)

            // Set icon based on hour (day or night icon)
            val hour = hourlyWeather.time.substring(0, 2).toInt()
            val icon = if (hour in 6..18) R.drawable.ic_day_hour else R.drawable.ic_night_hour
            weatherIconImageView.setImageResource(icon)
        }
    }

    class HourlyWeatherDiffCallback : DiffUtil.ItemCallback<HourlyWeatherData>() {
        override fun areItemsTheSame(oldItem: HourlyWeatherData, newItem: HourlyWeatherData): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: HourlyWeatherData, newItem: HourlyWeatherData): Boolean {
            return oldItem == newItem
        }
    }

    // Converts temperature based on selected unit
    private fun convertTemperature(tempInCelsius: Double, unit: String): Double {
        return when (unit) {
            CELSIUS_SHARED -> tempInCelsius
            FAHRENHEIT_SHARED -> (tempInCelsius * 9 / 5) + 32
            KELVIN_SHARED -> tempInCelsius + 273.15
            else -> tempInCelsius
        }
    }

    // Helper function to get the symbol of the temperature unit
    private fun getUnitSymbol(unit: String): String {
        return when (unit) {
            CELSIUS_SHARED -> "°C"
            FAHRENHEIT_SHARED -> "°F"
            KELVIN_SHARED -> "K"
            else -> "°C"
        }
    }
}
