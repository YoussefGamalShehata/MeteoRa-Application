package com.example.meteora.features.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.features.map.view.WeatherBasedLocationFragment
import com.example.meteora.model.Forcast

class FavoritesAdapter(
    private val onDeleteClickListener: (Forcast) -> Unit
) : ListAdapter<Forcast, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_forecast, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.bind(forecast, onDeleteClickListener) // Pass the delete listener to the holder
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationTextView: TextView = itemView.findViewById(R.id.location_text)
        private val temperatureTextView: TextView = itemView.findViewById(R.id.temperature_text)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)
        private val detailsButton: ImageButton = itemView.findViewById(R.id.details_button)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(forecast: Forcast, onDeleteClickListener: (Forcast) -> Unit) {
            locationTextView.text = forecast.city.name
            temperatureTextView.text = "Temperature: ${forecast.list[0].main.temp}°C"
            descriptionTextView.text = forecast.list[0].weather[0].description

            detailsButton.setOnClickListener {
                val weatherFragment = WeatherBasedLocationFragment().apply {
                    arguments = Bundle().apply {
                        putDouble("latitude", forecast.city.coord.lat)
                        putDouble("longitude", forecast.city.coord.lon)
                    }
                }
                val fragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                weatherFragment.show(fragmentManager, "weatherDetails")
            }

            deleteButton.setOnClickListener {
                onDeleteClickListener(forecast)
            }
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
