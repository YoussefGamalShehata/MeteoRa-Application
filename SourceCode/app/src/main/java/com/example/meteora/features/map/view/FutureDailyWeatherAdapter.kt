package com.example.meteora.features.map.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.model.DailyForecast

class FutureDailyWeatherAdapter :
    ListAdapter<DailyForecast, FutureDailyWeatherAdapter.DailyViewHolder>(DailyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_weather, parent, false)
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    class DailyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val minTempTextView: TextView = itemView.findViewById(R.id.minTempTextView)
        private val maxTempTextView: TextView = itemView.findViewById(R.id.maxTempTextView)


        fun bind(data: DailyForecast) {
            dateTextView.text = data.date
            minTempTextView.text = "Min: ${data.minTemp} °C"
            maxTempTextView.text = "Max: ${data.maxTemp} °C"
        }
    }

    class DailyWeatherDiffCallback : DiffUtil.ItemCallback<DailyForecast>() {
        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            // Assuming 'date' is unique for each daily data item
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem == newItem
        }
    }
}
