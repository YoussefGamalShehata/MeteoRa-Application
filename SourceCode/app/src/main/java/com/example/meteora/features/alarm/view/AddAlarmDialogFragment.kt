package com.example.meteora.features.alarm.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.meteora.R
import com.example.meteora.ui.home.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class AddAlarmDialogFragment(
    private val onAlarmAdded: (String, String) -> Unit,
    private val homeViewModel: HomeViewModel
) : DialogFragment() {

    private lateinit var alarmNameInput: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button
    private lateinit var timeTypeTextView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //var AlarmFlag = false
    private lateinit var radioAlarm: Button
    private lateinit var radioNotification: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_add_alarm, container, false)
        alarmNameInput = view.findViewById(R.id.alarmNameInput)
        timePicker = view.findViewById(R.id.timePicker)
        btnAdd = view.findViewById(R.id.btnAddAlarm)
        btnCancel = view.findViewById(R.id.btnCancel)
        timeTypeTextView = view.findViewById(R.id.timeTypeTextView)
        radioAlarm = view.findViewById(R.id.radioAlarm)
        radioNotification = view.findViewById(R.id.radioNotification)

        timePicker.setIs24HourView(false)
        updateTimeType()
        timePicker.setOnTimeChangedListener { _, hour, _ ->
            updateTimeType(hour)
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        btnAdd.setOnClickListener {
            val alarmName = alarmNameInput.text.toString()
            val alarmTime = String.format("%02d:%02d", timePicker.hour, timePicker.minute)

            // Fetch weather data using the current location
            fetchCurrentLocation { latitude, longitude ->
                homeViewModel.fetchCurrentWeather(latitude, longitude)
                onAlarmAdded(alarmName, alarmTime)
                dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
        return view
    }

    private fun updateTimeType(hour: Int = timePicker.hour) {
        timeTypeTextView.text = if (hour < 12) "AM" else "PM"
    }

    private fun fetchCurrentLocation(onLocationFetched: (Double, Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions if not granted
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permissions are granted, get the location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Use the location coordinates
                    onLocationFetched(location.latitude, location.longitude)
                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
