package com.example.project_coviz.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_coviz.R
import kotlinx.android.synthetic.main.resources_fragment.*
import kotlinx.android.synthetic.main.settings_fragment.*


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
        settingsHoursEditText.setText("120")

        settingsSubmitButton.setOnClickListener {
            submit()
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

    }
}