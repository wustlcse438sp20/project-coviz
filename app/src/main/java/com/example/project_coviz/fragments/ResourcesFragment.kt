package com.example.project_coviz.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.project_coviz.R
import kotlinx.android.synthetic.main.resources_fragment.*


private lateinit var tv : ArrayList<TextView>

class ResourcesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.resources_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv = arrayListOf()

        tv.add(cdc_org_TV)
        tv.add(cdc_faq_TV)
        tv.add(cdc_howto_TV)
        tv.add(cdc_ifsick_TV)
        tv.add(cdc_prevent_TV)
        tv.add(cdc_symptoms_TV)
        tv.add(google_org_TV)
        tv.add(google_r_TV)
        tv.add(idsa_org_TV)
        tv.add(idsa_r_TV)
        tv.add(aha_org_TV)
        tv.add(aha_r_TV)
        tv.add(cmi_org_TV)
        tv.add(cmi_r_TV)
        tv.add(mha_org_TV)
        tv.add(mha_r_TV)
        tv.forEach {
            it.setMovementMethod(LinkMovementMethod.getInstance())}

    }
}