import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.model.Forcast

class FavoritesAdapter : ListAdapter<Forcast, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_forecast, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.bind(forecast)
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationTextView: TextView = itemView.findViewById(R.id.location_text)
        private val temperatureTextView: TextView = itemView.findViewById(R.id.temperature_text)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)

        fun bind(forecast: Forcast) {
            // Set the location text
            locationTextView.text = forecast.city.name // Replace with the actual property for location

            // Set the temperature text, assuming temperature is a property in forecast.list[0].main.temp
            temperatureTextView.text = "Temperature: ${forecast.list[0].main.temp}°C"

            // Set the description text, assuming description is a property in forecast.list[0].weather[0].description
            descriptionTextView.text = forecast.list[0].weather[0].description
        }
    }

    class FavoriteDiffCallback : DiffUtil.ItemCallback<Forcast>() {
        override fun areItemsTheSame(oldItem: Forcast, newItem: Forcast): Boolean {
            return oldItem.list[0].weather[0].id == newItem.list[0].weather[0].id
        }

        override fun areContentsTheSame(oldItem: Forcast, newItem: Forcast): Boolean {
            return oldItem == newItem
        }
    }
}