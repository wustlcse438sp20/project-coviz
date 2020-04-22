package com.example.project_coviz.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
            disclosureToggleSwitch.isEnabled = false
            disclosureDatePicker.isEnabled = false
            disclosureSubmitButton.isEnabled = false
        }

        disclosureSubmitButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.context!!)
            alertDialog.setMessage(resources.getString(R.string.disclosure_warning))
                .setCancelable(false)
                .setPositiveButton("Yes", DialogInterface.OnClickListener {
                        dialog, i ->
                    disclose()
                })
                .setNegativeButton("No", DialogInterface.OnClickListener{
                        dialog, id -> dialog.cancel()
                })
            val warning = alertDialog.create()
            warning.setTitle("Are you sure?")
            warning.show()

        }


    }

    private fun disclose() {
        val disclose: Boolean = disclosureToggleSwitch.isChecked
        if (!disclose) {
            Toast.makeText(this.context, "To disclose your own COVID-19 exposure you must toggle the switch, first.", Toast.LENGTH_LONG).show()
            return
        }

        val date = of(
            disclosureDatePicker.year,
            disclosureDatePicker.month+1,
            disclosureDatePicker.dayOfMonth
        )
        val minDate = of(2019, 12, 1)
        val today = now()

        if(date.isAfter(today)) {
            Toast.makeText(this.context, "Date is in the future.", Toast.LENGTH_LONG).show()
            return
        }
        if(date.isBefore(minDate)) {
            Toast.makeText(this.context, "Date appears to predate COVID-19.", Toast.LENGTH_LONG).show()
            return
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
                    { Toast.makeText(this.activity, resources.getString(R.string.disclosure_gratitude), Toast.LENGTH_LONG).show() }
                )
            }
        }

        disclosureToggleSwitch.isEnabled = false
        disclosureDatePicker.isEnabled = false
        //Turn off notifications
        Settings.notify = false
    }
}