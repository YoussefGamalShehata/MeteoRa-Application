package com.example.meteora.features.alarm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.meteora.R

class AddAlarmDialogFragment(private val onAlarmAdded: (String, String) -> Unit) : DialogFragment() {

    private lateinit var alarmNameInput: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button
    private lateinit var timeTypeTextView: TextView

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
        timePicker.setIs24HourView(false)
        updateTimeType()
        timePicker.setOnTimeChangedListener { _, hour, _ ->
            updateTimeType(hour)
        }
        btnAdd.setOnClickListener {
            val alarmName = alarmNameInput.text.toString()
            val alarmTime = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
            onAlarmAdded(alarmName, alarmTime)
            dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
        return view
    }
    private fun updateTimeType(hour: Int = timePicker.hour) {
        timeTypeTextView.text = if (hour < 12) "AM" else "PM"
    }
}
