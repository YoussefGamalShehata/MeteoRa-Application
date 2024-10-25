import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.model.HourlyWeatherData

class HourlyWeatherListAdapter : ListAdapter<HourlyWeatherData, HourlyWeatherListAdapter.HourlyWeatherViewHolder>(HourlyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_weather, parent, false)
        return HourlyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val hourlyWeather = getItem(position)
        holder.bind(hourlyWeather)
    }

    class HourlyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(hourlyWeather: HourlyWeatherData) {
            timeTextView.text = hourlyWeather.time
            tempTextView.text = "${hourlyWeather.temp} °C"
            descriptionTextView.text = hourlyWeather.description.capitalize()
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
}
