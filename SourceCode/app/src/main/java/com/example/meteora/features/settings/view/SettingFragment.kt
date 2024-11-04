// SettingFragment.kt
package com.example.meteora.features.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.meteora.R
import com.example.meteora.data.SettingControl
import com.example.meteora.helpers.Constants

class SettingFragment : Fragment() {

    private lateinit var settingControl: SettingControl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        settingControl = SettingControl(requireContext())

        // Initialize UI components
        setupTemperatureUnitRadioButtons(view)
        setupWindSpeedUnitRadioButtons(view)
        setupLanguageRadioButtons(view)

        return view
    }

    private fun setupTemperatureUnitRadioButtons(view: View) {
        val radioCelsius: RadioButton = view.findViewById(R.id.radioCelsius)
        val radioFahrenheit: RadioButton = view.findViewById(R.id.radioFahrenheit)
        val radioKelvin: RadioButton = view.findViewById(R.id.radioKelvin)

        // Load and set initial state
        radioCelsius.isChecked = settingControl.getTemperatureUnit() == Constants.CELSIUS_SHARED
        radioFahrenheit.isChecked = settingControl.getTemperatureUnit() == Constants.FAHRENHEIT_SHARED
        radioKelvin.isChecked = settingControl.getTemperatureUnit() == Constants.KELVIN_SHARED

        radioCelsius.setOnClickListener { settingControl.setTemperatureUnit(Constants.CELSIUS_SHARED) }
        radioFahrenheit.setOnClickListener { settingControl.setTemperatureUnit(Constants.FAHRENHEIT_SHARED) }
        radioKelvin.setOnClickListener { settingControl.setTemperatureUnit(Constants.KELVIN_SHARED) }
    }

    private fun setupWindSpeedUnitRadioButtons(view: View) {
        val radioMeterPerSecond: RadioButton = view.findViewById(R.id.radioMeterPerSecond)
        val radioMilesPerHour: RadioButton = view.findViewById(R.id.radioMilesPerHour)
        val radioFahrenheit: RadioButton = view.findViewById(R.id.radioFahrenheit)

        // Load and set initial state
        radioMeterPerSecond.isChecked = settingControl.getWindSpeedUnit() == Constants.UNITS_METRIC
        radioMilesPerHour.isChecked = settingControl.getWindSpeedUnit() == Constants.UNITS_IMPERIAL

        radioMeterPerSecond.setOnClickListener {
            settingControl.setWindSpeedUnit(Constants.UNITS_METRIC)
        }

        radioMilesPerHour.setOnClickListener {
            settingControl.setWindSpeedUnit(Constants.UNITS_IMPERIAL)

            // Check Fahrenheit when Miles/hour is selected
            radioFahrenheit.isChecked = true
            settingControl.setTemperatureUnit(Constants.FAHRENHEIT_SHARED)
        }
    }


    private fun setupLanguageRadioButtons(view: View) {
        val radioEnglish: RadioButton = view.findViewById(R.id.radioEnglish)
        val radioArabic: RadioButton = view.findViewById(R.id.radioArabic)

        // Load and set initial state
        radioEnglish.isChecked = settingControl.getLanguage() == Constants.ENGLISH_SHARED
        radioArabic.isChecked = settingControl.getLanguage() ==  Constants.ARABIC_SHARED

        radioEnglish.setOnClickListener { settingControl.setLanguage("en") }
        radioArabic.setOnClickListener { settingControl.setLanguage("ar") }
    }
}
