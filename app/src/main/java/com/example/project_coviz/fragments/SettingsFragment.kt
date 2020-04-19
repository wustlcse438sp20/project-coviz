package com.example.project_coviz.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_coviz.R
import com.example.project_coviz.Settings
import com.example.project_coviz.UnifiedLocationTrackService
import kotlinx.android.synthetic.main.settings_fragment.*
import kotlinx.android.synthetic.main.settings_fragment.enableNotificationsSwitch
import kotlinx.android.synthetic.main.settings_fragment.stopTrackingButton


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Need to access current settings and set them:
        settingsHoursEditText.setText(Settings.HOURS_OF_DATA.toString())

        settingsSubmitButton.setOnClickListener {
            submit()
        }

        enableNotificationsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                Settings.notify = isChecked
        }

        stopTrackingButton.setOnClickListener {
            this.activity?.stopService(Intent(this.activity, UnifiedLocationTrackService::class.java))
        }
    }

    private fun submit() {
        if (settingsHoursEditText.text.isEmpty()) {
            //Toast?
            return
        }

        var hours = settingsHoursEditText.text.trim().toString().toInt()
        if (hours > resources.getInteger(R.integer.max_hours)) {
            Toast.makeText(
                this.context,
                "${hours} hours exceeds maximum, ${resources.getInteger(R.integer.max_hours)}",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (hours < resources.getInteger(R.integer.min_hours)) {
            Toast.makeText(
                this.context,
                "${hours} hours needs to be greater than minimum, ${resources.getInteger(R.integer.min_hours)}",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        Settings.HOURS_OF_DATA = hours

    }
}