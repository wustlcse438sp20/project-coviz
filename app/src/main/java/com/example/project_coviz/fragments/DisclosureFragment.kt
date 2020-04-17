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
import kotlinx.android.synthetic.main.disclosure_fragment.*
import kotlinx.android.synthetic.main.resources_fragment.*
import java.time.LocalDate
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
        disclosureDirections.setMovementMethod(LinkMovementMethod.getInstance())
        disclosureSubmitButton.setOnClickListener {
            submitDisclosure()
        }
    }

    private fun submitDisclosure() {
        var disclose: Boolean = disclosureToggleSwitch.isChecked
        val date = LocalDate.of(
            disclosureDatePicker.year,
            disclosureDatePicker.month + 1,
            disclosureDatePicker.dayOfMonth
        )
        val minDate = LocalDate.of(2019, 12, 1)
        val today = LocalDate.now()
        if (!disclose) {
            Toast.makeText(
                this.context,
                "To disclose your own COVID-19 exposure you must toggle the switch, first.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (date.isAfter(today)) {
            Toast.makeText(this.context, "Date is in the future.", Toast.LENGTH_LONG).show()
            return
        }
        if (date.isBefore(minDate)) {
            Toast.makeText(this.context,"Date appears to predate COVID-19.", Toast.LENGTH_LONG)
                .show()
            return


        }
    }

}