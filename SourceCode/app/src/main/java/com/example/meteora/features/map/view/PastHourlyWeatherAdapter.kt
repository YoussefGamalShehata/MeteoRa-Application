package com.example.meteora.features.map.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.model.HourlyWeatherData

class PastHourlyWeatherAdapter :
    ListAdapter<HourlyWeatherData, PastHourlyWeatherAdapter.HourlyViewHolder>(HourlyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_weather, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val temperatureTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(data: HourlyWeatherData) {
            timeTextView.text = data.time
            temperatureTextView.text = "${data.temp} °C"
            descriptionTextView.text = data.description
        }
    }

    class HourlyWeatherDiffCallback : DiffUtil.ItemCallback<HourlyWeatherData>() {
        override fun areItemsTheSame(oldItem: HourlyWeatherData, newItem: HourlyWeatherData): Boolean {
            // Assuming 'time' is unique for each hourly data item
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: HourlyWeatherData, newItem: HourlyWeatherData): Boolean {
            return oldItem == newItem
        }
    }
}
