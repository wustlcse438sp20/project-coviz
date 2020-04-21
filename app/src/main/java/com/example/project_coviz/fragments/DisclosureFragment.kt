package com.example.project_coviz.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_coviz.R
import com.example.project_coviz.Settings
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.api.LocationAndTimestamp
import com.example.project_coviz.api.LocationAndTimestampData
import com.example.project_coviz.db.LocRepository
import com.example.project_coviz.db.LocRoomDatabase
import kotlinx.android.synthetic.main.disclosure_fragment.*
import kotlinx.android.synthetic.main.resources_fragment.*
import java.time.LocalDate
import java.time.LocalDate.*
import java.time.ZoneId
import java.util.*

class DisclosureFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.disclosure_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Activate URL
        disclosureDirections.setMovementMethod(LinkMovementMethod.getInstance())

        //Load values stored in Settings object
        disclosureToggleSwitch.setChecked(Settings.disclosure)
        if (Settings.disclosure) {
            disclosureDatePicker.updateDate(
                Settings.disclose_year,
                Settings.disclose_month,
                Settings.disclose_day)
        }

        disclosureSubmitButton.setOnClickListener {
            val disclose: Boolean = disclosureToggleSwitch.isChecked
            val date = of(
                disclosureDatePicker.year,
                disclosureDatePicker.month+1,
                disclosureDatePicker.dayOfMonth
            )
            val minDate = of(2019, 12, 1)
            val today = now()
            if (!disclose) {
                Toast.makeText(it.context, "To disclose your own COVID-19 exposure you must toggle the switch, first.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(date.isAfter(today)) {
                Toast.makeText(it.context, "Date is in the future.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(date.isBefore(minDate)) {
                Toast.makeText(it.context, "Date appears to predate COVID-19.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            //Store values
            Settings.disclosure = disclose
            Settings.disclose_year = disclosureDatePicker.year
            Settings.disclose_month = disclosureDatePicker.month
            Settings.disclose_day = disclosureDatePicker.dayOfMonth

            //send the data
            val locationRepo = LocRepository(LocRoomDatabase.getDatabase(this.context!!).locDao())
            val laterThan = Date.from(date.atStartOfDay().atZone( ZoneId.systemDefault()).toInstant())
            locationRepo.getLatestLocations(laterThan) {
                    locs ->
                    val data = locs.map { LocationAndTimestamp(it.cell_token, it.time) }
                    if(data.size > 0) {
                        ApiClient.APIRepository.postLocationAndTimestamps(
                            LocationAndTimestampData(
                                data
                            )
                        )
                        this.activity?.runOnUiThread(
                            { Toast.makeText(this.activity, "We have notified our servers. Thank you for helping others avoid exposure.", Toast.LENGTH_LONG).show() }
                        )
                    }
            }


            //Turn off notifications
            Settings.notify = false
        }


    }
}