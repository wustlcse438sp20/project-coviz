package com.example.project_coviz

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.api.ApiRepository
import com.example.project_coviz.api.LocationAndTimestampData
import com.example.project_coviz.s2.S2CellId
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var heatMapProvider : HeatmapTileProvider
    private lateinit var heatMapOverlay: TileOverlay
    var PERMISSION_ID: Int = 45
    lateinit var userLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onResume() {
        super.onResume()
        val tok = LatestLocation.getLatestCellToken()
        if(tok != null) {
            ApiClient.APIRepository.updateTimestampsForCurrentLocation(tok)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        ApiClient.APIRepository.locationAndTimestamps.observe(this, androidx.lifecycle.Observer<LocationAndTimestampData> { ltData ->

                addHeatMap(ltData.data.map {
                    val cell = S2CellId.fromToken(it.cell_token).toLatLng()
                    LatLng(cell.latDegrees(), cell.lngDegrees())
                })
                val location = LatestLocation.getLatestLocation()

                if(location != null) {
                    userLatLng = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(userLatLng).title("User Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng))
                    mMap.setMyLocationEnabled(true)
                    Log.d("Location", "User Lat Long:" + userLatLng.toString())
                }


        })

        val tok = LatestLocation.getLatestCellToken()
        if(tok != null) {
            ApiClient.APIRepository.updateTimestampsForCurrentLocation(tok)
        }


    }

    private fun addHeatMap(list: List<LatLng>) {
        heatMapProvider = HeatmapTileProvider.Builder()
            .data(list)
            .build()
        // Add a tile overlay to the map, using the heat map tile provider.
        heatMapOverlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))
    }

    private fun updateHeatMap(data: List<LatLng>) {
        // TODO Get updated location data to update heatmap
        heatMapProvider.setData(data)
        heatMapOverlay.clearTileCache()
    }

}
