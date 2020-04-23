package com.example.project_coviz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_coviz.*
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.api.LocationAndTimestampData
import com.example.project_coviz.db.LocRepository
import com.example.project_coviz.s2.S2CellId
import com.example.project_coviz.s2.S2LatLng
import kotlinx.android.synthetic.main.prediction_fragment.*
import kotlin.collections.HashMap

class PredictionFragment : Fragment() {

    lateinit var adapter: WatchlistAdapter
    private var olderCases = MutableLiveData<LocationAndTimestampData>()
    private var newerCases = MutableLiveData<LocationAndTimestampData>()
    private var watchlist: HashMap<S2LatLng, Int> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        return inflater.inflate(R.layout.prediction_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newerCases.observe(this, Observer {
            // count number of cases in each cell for each set and get top n cells that have highest change in cases
            for (case in it.data) {
                val caseLatLng = S2CellId.fromToken(case.cell_token).toLatLng()
                if (watchlist.containsKey(caseLatLng)) {
                    watchlist.put(caseLatLng, watchlist.get(caseLatLng)!! + 1)
                } else {
                    watchlist.put(caseLatLng, 1)
                }
            }
            adapter.notifyDataSetChanged()
        })
        adapter = WatchlistAdapter(watchlist.toList())
        highest_rise.adapter = adapter
        highest_rise.layoutManager = LinearLayoutManager(this.context)

//        olderCases.observe(this, Observer {
            // call update on sencond live mutable data
        ApiClient.APIRepository.getLocationAndTimestamps(newerCases,LatestLocation.getLatestCellToken()!!,Settings.WATCHLIST_HOUR)
//    })
    }

    override fun onStart() {
        super.onStart()

        if(LatestLocation.getLatestCellToken() == null){

        }
        else{
            ApiClient.APIRepository.getLocationAndTimestamps(olderCases,LatestLocation.getLatestCellToken()!!,Settings.HOURS_OF_DATA)
        }

//        val calendar: Calendar = Calendar.getInstance()
//        calendar.add(Calendar.HOUR, -1 * Settings.HOURS_OF_DATA)
//        val date: Date = calendar.time
//        locationRepo = LocRepository(LocRoomDatabase.getDatabase(activity as MapsActivity).locDao())
//        ApiClient.APIRepository.locationAndTimestamps.observe(this, androidx.lifecycle.Observer {
//            var infectionLocations = it;
//            locationRepo.getLatestLocations(date) { locations ->
//                for (infectionLocation in infectionLocations.data) {
//                    for (userLocation in locations) {
//                        watchlist.clear()
//                        var token = S2CellId.fromToken(userLocation.cell_token).toLatLng()
//                        if (S2CellId.fromToken(infectionLocation.cell_token).intersects(
//                                S2CellId.fromToken(
//                                    userLocation.cell_token
//                                )
//                            )
//                        ) {
//                            if (watchlist.containsKey(token)) {
//                                watchlist.put(token, watchlist.get(token)!! + 1)
//                            } else {
//                                watchlist.put(
//                                    S2CellId.fromToken(userLocation.cell_token).toLatLng(),
//                                    1
//                                )
//                            }
//
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged()
//            }
//        })
//        adapter = WatchlistAdapter(watchlist.toList())
//
//        highest_rise.adapter = adapter
//
//        highest_rise.layoutManager = LinearLayoutManager(this.context)
    }

}