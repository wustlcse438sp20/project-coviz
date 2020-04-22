package com.example.project_coviz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.project_coviz.R
import com.example.project_coviz.WatchlistAdapter
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.api.LocationAndTimestampData
import com.google.android.gms.maps.model.LatLng

class PredictionFragment : Fragment() {

    lateinit var adapter: WatchlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        return inflater.inflate(R.layout.prediction_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        var cases: LocationAndTimestampData
        var watchlist: List<Pair<LatLng, Int>> = ArrayList()

//        ApiClient.APIRepository.getLocationAndTimestamps()       TODO: get api call for all cases regardless of s2 cell
    }

}