package com.example.meteora.features.alarm.viewModel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meteora.features.alarm.broadcastReceiver.AlarmReceiver
import com.example.meteora.model.Alarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AlarmViewModel(private val context: Context) : ViewModel() {

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> get() = _alarms

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            val sharedPrefs = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
            val savedAlarms = mutableListOf<Alarm>()

            sharedPrefs.all.forEach { (key, time) ->
                if (key.toLongOrNull() != null) {
                    val id = key.toLong()
                    val alarmTime = time.toString()
                    val name = sharedPrefs.getString("${id}_name", "Alarm $id") ?: "Alarm $id" // Get the saved name or default
                    savedAlarms.add(Alarm(id, name, alarmTime))
                } else {
                    Log.w("AlarmDebug", "Ignoring invalid alarm key: $key")
                }
            }

            _alarms.value = savedAlarms
        }
    }


    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch {
            _alarms.value = _alarms.value + alarm
            saveAlarm(alarm)
        }
    }

    fun removeAlarm(alarm: Alarm) {
        viewModelScope.launch {
            _alarms.value = _alarms.value.filter { it.id != alarm.id }
            removeAlarmFromStorage(alarm)
        }
    }

    private fun saveAlarm(alarm: Alarm) {
        val sharedPrefs = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString(alarm.id.toString(), alarm.time)
            putString("${alarm.id}_name", alarm.name) // Save the alarm name
        }.apply()
    }


    private fun removeAlarmFromStorage(alarm: Alarm) {
        val sharedPrefs = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().remove(alarm.id.toString()).apply()
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmCalendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

        Log.d("AlarmDebug", "Setting alarm time: ${alarm.time}")

        val alarmTime = sdf.parse(alarm.time)
        if (alarmTime != null) {
            alarmCalendar.set(Calendar.HOUR_OF_DAY, alarmTime.hours)
            alarmCalendar.set(Calendar.MINUTE, alarmTime.minutes)
            alarmCalendar.set(Calendar.SECOND, 0)
            alarmCalendar.set(Calendar.MILLISECOND, 0)

            if (alarmCalendar.timeInMillis <= System.currentTimeMillis()) {
                alarmCalendar.add(Calendar.DAY_OF_YEAR, 1)  // Schedule for the next day
            }

            Log.d("AlarmDebug", "Scheduled alarm for: ${alarmCalendar.time}")
            Log.d("AlarmDebug", "Current time: ${Date()}")

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("alarmName", alarm.name)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmCalendar.timeInMillis,
                pendingIntent
            )
        } else {
            Log.e("AlarmDebug", "Failed to parse alarm time: ${alarm.time}")
        }
    }


}