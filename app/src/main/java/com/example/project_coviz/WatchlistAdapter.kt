package com.example.project_coviz

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_coviz.api.LocationAndTimestampData
import com.google.android.gms.maps.model.LatLng

class WatchlistViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.watchlist_item, parent, false)) {

    private val locationName: TextView
    private val locationCases: TextView

    init {
        locationName = itemView.findViewById(R.id.watchlist_location_name)
        locationCases = itemView.findViewById(R.id.watchlist_location_cases)
    }

    fun bind(cell: Pair<LatLng, Int>) {
        locationName.text = cell.first.toString()
        locationCases.text = cell.second.toString()
    }

}

class WatchlistAdapter(private val locations: List<Pair<LatLng, Int>>) :
        RecyclerView.Adapter<WatchlistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : WatchlistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WatchlistViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        val cell: Pair<LatLng, Int> = locations[position]
        holder.bind(cell)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

}