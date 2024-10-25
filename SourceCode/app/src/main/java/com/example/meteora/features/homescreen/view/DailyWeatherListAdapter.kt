import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.model.DailyForecast

class DailyWeatherListAdapter : ListAdapter<DailyForecast, DailyWeatherListAdapter.DailyWeatherViewHolder>(DailyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_weather, parent, false)
        return DailyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val dailyForecast = getItem(position)
        holder.bind(dailyForecast)
    }

    class DailyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val maxTempTextView: TextView = itemView.findViewById(R.id.maxTempTextView)
        private val minTempTextView: TextView = itemView.findViewById(R.id.minTempTextView)

        fun bind(dailyForecast: DailyForecast) {
            dateTextView.text = dailyForecast.date
            maxTempTextView.text = "Max: ${dailyForecast.maxTemp} °C"
            minTempTextView.text = "Min: ${dailyForecast.minTemp} °C"
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
}
