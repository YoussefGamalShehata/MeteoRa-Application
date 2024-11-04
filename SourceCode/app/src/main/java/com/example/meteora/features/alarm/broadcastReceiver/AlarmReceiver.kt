package com.example.meteora.features.alarm.broadcastReceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.meteora.MainActivity
import com.example.meteora.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmName = intent.getStringExtra("alarmName") ?: "Alarm"

        val sharedPrefs = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        val temperature = sharedPrefs.getString("weather_temperature", "N/A")
        val weatherDescription = sharedPrefs.getString("weather_description", "N/A")
        val weatherCityName = sharedPrefs.getString("weather_city_name", "N/A")

        val weatherInfo = "Temp: $temperature," +
                          "Sky: $weatherDescription," +
                          "City: $weatherCityName"

        Toast.makeText(context, "Alarm Triggered: $alarmName", Toast.LENGTH_LONG).show()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel_id"
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm notifications"
                setSound(alarmSound, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alarm Triggered: $alarmName")
            .setContentText(weatherInfo)
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
