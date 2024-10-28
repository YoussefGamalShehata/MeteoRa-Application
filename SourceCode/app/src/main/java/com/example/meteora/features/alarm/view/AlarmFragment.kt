package com.example.meteora.features.alarm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.features.alarm.viewModel.AlarmViewModel
import com.example.meteora.features.alarm.viewModel.AlarmViewModelFactory
import com.example.meteora.model.Alarm

class AlarmFragment : Fragment() {

    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var btnAddAlarm: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        alarmRecyclerView = view.findViewById(R.id.alarmRecyclerView)
        btnAddAlarm = view.findViewById(R.id.btnAddAlarm)

        alarmViewModel = ViewModelProvider(this, AlarmViewModelFactory(requireContext()))[AlarmViewModel::class.java]

        alarmAdapter = AlarmAdapter { alarm ->
            alarmViewModel.removeAlarm(alarm)
        }
        alarmRecyclerView.layoutManager = LinearLayoutManager(context)
        alarmRecyclerView.adapter = alarmAdapter

        btnAddAlarm.setOnClickListener {
            val dialog = AddAlarmDialogFragment { alarmName, alarmTime ->
                val alarmId = System.currentTimeMillis()
                val newAlarm = Alarm(alarmId, alarmName, alarmTime)
                alarmViewModel.addAlarm(newAlarm)
                alarmViewModel.scheduleAlarm(newAlarm)
            }
            dialog.show(parentFragmentManager, "AddAlarmDialog")
        }

        lifecycleScope.launchWhenStarted {
            alarmViewModel.alarms.collect { alarms ->
                alarmAdapter.submitList(alarms)
            }
        }

        return view
    }
}