package com.example.project_coviz

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.project_coviz.api.ApiClient
import com.example.project_coviz.api.LocationAndTimestampData
import com.example.project_coviz.s2.S2CellId
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var heatMapProvider : HeatmapTileProvider
    private lateinit var heatMapOverlay: TileOverlay
    private lateinit var toolBar: Toolbar
    var PERMISSION_ID: Int = 45
    lateinit var userLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        toolBar = my_toolbar
        setSupportActionBar(my_toolbar)


//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         var menuInflater : MenuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> Toast.makeText(this, "You selected settings!", Toast.LENGTH_LONG).show()
            R.id.resources -> Toast.makeText(this, "You selected resources!", Toast.LENGTH_LONG).show()
            else -> Toast.makeText(this, "You must have clicked on the main menu!", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
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
                    Log.d("LATLNG","Lat: "+ cell.latDegrees()+" Long: " +cell.lngDegrees())
                    LatLng(cell.latDegrees(), cell.lngDegrees())
                })
                val location = LatestLocation.getLatestLocation()

                if(location != null) {
                    userLatLng = LatLng(location.latitude, location.longitude)
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
        Log.d("LIST",list.toString())
        if(list.isEmpty()){
            Log.d("TEST"," list empty")
            return
        }
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
