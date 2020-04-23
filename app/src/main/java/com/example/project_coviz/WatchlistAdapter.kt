package com.example.project_coviz

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_coviz.s2.S2LatLng

class WatchlistViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.watchlist_item, parent, false)) {

    private val locationName: TextView
    private val locationCases: TextView

    init {
        locationName = itemView.findViewById(R.id.watchlist_location_name)
        locationCases = itemView.findViewById(R.id.watchlist_location_cases)
    }

    fun bind(cell: Pair<S2LatLng, Int>) {
        locationName.text = cell.first.latDegrees().toString() + ", " + cell.first.lngDegrees().toString()
        locationCases.text = cell.second.toString()
    }

}

class WatchlistAdapter(private val locations: Map<S2LatLng, Int>) :
        RecyclerView.Adapter<WatchlistViewHolder>() {

    private lateinit var locationsList: List<Pair<S2LatLng, Int>>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : WatchlistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        locationsList = locations.toList()
        return WatchlistViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        val cell: Pair<S2LatLng, Int> = locationsList[position]
        holder.bind(cell)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

}