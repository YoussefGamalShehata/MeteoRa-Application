package com.example.meteora.features.alarm.viewModel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meteora.R
import com.example.meteora.features.alarm.broadcastReceiver.AlarmReceiver
import com.example.meteora.features.alarm.view.AddAlarmDialogFragment
import com.example.meteora.model.Alarm
import com.example.meteora.model.Weather
import com.example.meteora.network.ApiState
import com.example.meteora.ui.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmViewModel(
    @SuppressLint("StaticFieldLeak") private val context: Context,
    private val homeViewModel: HomeViewModel
) : ViewModel() {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)

    // Holds the list of alarms
    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> get() = _alarms

    init {
        loadWeatherInfo()
    }

    private fun loadWeatherInfo() {
        viewModelScope.launch {
            homeViewModel.weatherData.collect { apiState ->
                if (apiState is ApiState.Success) {
                    saveWeatherInfo(apiState.data)
                }
            }
        }
    }

    private fun saveWeatherInfo(weather: Weather) {
        sharedPrefs.edit().apply {
            putString("weather_temperature", "${weather.main.temp}°C")
            putString("weather_description", weather.weather[0].description)
            putString("weather_wind_speed", "Wind Speed: ${weather.wind.speed} m/s")
            putString("weather_clouds", "Clouds: ${weather.clouds.all}%")
            putString("weather_city_name", weather.name)

        }.apply()
    }

    fun addAlarm(alarm: Alarm) {
        _alarms.value = _alarms.value + alarm
    }

   //  Remove an alarm from the list
    fun removeAlarm(alarm: Alarm) {
        _alarms.value = _alarms.value - alarm
        cancelAlarm(alarm) // Also cancel the alarm when it's removed
    }

    // Schedule the alarm
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_NAME", alarm.name)
            putExtra("ALARM_TIME", alarm.time) // Keep this if you need to retrieve it
        }

        // Calculate the time in milliseconds for the alarm
        val calendar = Calendar.getInstance().apply {
            set(
                Calendar.HOUR_OF_DAY,
                alarm.time.split(":")[0].toInt()
            ) // Use the hour part of your alarm
            set(
                Calendar.MINUTE,
                alarm.time.split(":")[1].toInt()
            )     // Use the minute part of your alarm
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Adjust for the next occurrence if the time has already passed today
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm with the calculated time in milliseconds

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    }

    // Cancel a scheduled alarm
    private fun cancelAlarm(alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent) // Cancel the alarm
    }
}


