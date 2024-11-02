import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.meteora.model.DailyForecast

class DailyWeatherListAdapter(
    private val settingControl: SettingControl
) : ListAdapter<DailyForecast, DailyWeatherListAdapter.DailyWeatherViewHolder>(DailyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_home, parent, false)
        return DailyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val dailyForecast = getItem(position)

        // Get temperature unit and convert temperatures accordingly
        val unit = settingControl.getTemperatureUnit()
        val maxTemp = convertTemperature(dailyForecast.maxTemp, unit)
        val minTemp = convertTemperature(dailyForecast.minTemp, unit)
        holder.bind(dailyForecast, maxTemp, minTemp, getUnitSymbol(unit))
    }

    class DailyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val maxTempTextView: TextView = itemView.findViewById(R.id.maxTempTextView)
        private val minTempTextView: TextView = itemView.findViewById(R.id.minTempTextView)

        fun bind(dailyForecast: DailyForecast, maxTemp: Double, minTemp: Double, unitSymbol: String) {
            dateTextView.text = dailyForecast.date
            maxTempTextView.text = String.format(TEMPERATURE_FORMAT, maxTemp, unitSymbol)
            minTempTextView.text = String.format(TEMPERATURE_FORMAT, minTemp, unitSymbol)
        }
    }

    class DailyWeatherDiffCallback : DiffUtil.ItemCallback<DailyForecast>() {
        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
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
