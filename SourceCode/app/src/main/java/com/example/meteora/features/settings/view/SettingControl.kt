// SettingControl.kt
package com.example.meteora.data

import android.content.Context
import android.content.SharedPreferences

class SettingControl(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    companion object {
        const val TEMP_UNIT = "temperature_unit"
        const val WIND_UNIT = "wind_speed_unit"
        const val LANGUAGE = "language"
        const val FIRST_LAUNCH = "first_launch"
    }

    init {
        initializeDefaults()
    }

    private fun initializeDefaults() {
        if (sharedPreferences.getBoolean(FIRST_LAUNCH, true)) {
            setTemperatureUnit("Celsius") // Default temperature unit
            setWindSpeedUnit("meter/sec")  // Default wind speed unit
            setLanguage("English")          // Default language
            setFirstLaunch(false)
        }
    }

    private fun setFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferences.edit().putBoolean(FIRST_LAUNCH, isFirstLaunch).apply()
    }

    // Temperature Unit
    fun setTemperatureUnit(unit: String) {
        sharedPreferences.edit().putString(TEMP_UNIT, unit).apply()
    }

    fun getTemperatureUnit(): String {
        return sharedPreferences.getString(TEMP_UNIT, "Celsius") ?: "Celsius"
    }

    // Wind Speed Unit
    fun setWindSpeedUnit(unit: String) {
        sharedPreferences.edit().putString(WIND_UNIT, unit).apply()
    }

    fun getWindSpeedUnit(): String {
        return sharedPreferences.getString(WIND_UNIT, "meter/sec") ?: "meter/sec"
    }

    // Language
    fun setLanguage(language: String) {
        sharedPreferences.edit().putString(LANGUAGE, language).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(LANGUAGE, "English") ?: "English"
    }
}
