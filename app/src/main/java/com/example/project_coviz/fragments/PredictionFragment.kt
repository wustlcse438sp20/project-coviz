package com.example.project_coviz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_coviz.MapsActivity
import com.example.project_coviz.R
import com.example.project_coviz.Settings
import com.example.project_coviz.WatchlistAdapter
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.api.LocationAndTimestampData
import com.example.project_coviz.db.LocRepository
import com.example.project_coviz.db.LocRoomDatabase
import com.example.project_coviz.s2.S2CellId
import com.example.project_coviz.s2.S2LatLng
import kotlinx.android.synthetic.main.prediction_fragment.*
import java.util.*
import kotlin.collections.HashMap

class PredictionFragment : Fragment() {

    lateinit var adapter: WatchlistAdapter
    lateinit var locationRepo: LocRepository

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

        var watchlist: HashMap<S2LatLng, Int> = HashMap()

        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, -1 * Settings.HOURS_OF_DATA)
        val date: Date = calendar.time
        var casesLiveData: MutableLiveData<LocationAndTimestampData> = MutableLiveData()
        locationRepo = LocRepository(LocRoomDatabase.getDatabase(activity as MapsActivity).locDao())
        casesLiveData.observe(activity as MapsActivity, Observer { cases ->
            locationRepo.getLatestLocations(date) { locations ->
                for (userLocation in locations) {
                        watchlist.clear()
                        for (case in casesLiveData.value!!.data) {
                            watchlist.put(S2CellId.fromToken(case.cell_token).toLatLng(), 1)
                        }
                        adapter.notifyDataSetChanged()
                    ApiClient.APIRepository.getLocationAndTimestamps(casesLiveData, userLocation.cell_token)
                }
            }
        })

        adapter = WatchlistAdapter(watchlist.toList())

        highest_rise.adapter = adapter

        highest_rise.layoutManager = LinearLayoutManager(this.context)
    }

}