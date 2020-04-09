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
import org.json.JSONArray
import org.json.JSONException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        addHeatMap()
        // Add a marker in Sydney and move the camera
        getLastLocation()
    }

    //    Getting phone location information
    fun checkLocationPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        // BACKGROUND PERMISSION STUFF (don't need background location unless they have covid and want to share location)
//            &&
//            ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        }
        return false;
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
                //android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_ID -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permision granted
                    getLastLocation()
                } else {
                    //permission denied
                }
            }
        }
    }

    private fun getLastLocation() {
        if (checkLocationPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location
                        if (location == null) {
                            Log.d("Location", "Location is null")
                            LocationRequest()
                        }else {
                            userLatLng = LatLng(location!!.latitude, location!!.longitude)
                            mMap.addMarker(MarkerOptions().position(userLatLng).title("User Location"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng))
                            mMap.setMyLocationEnabled(true)
                            Log.d("Location","User Lat Long:" +userLatLng.toString())
                            LocationRequest()
                        }
                    }
            }else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else {
            requestLocationPermissions()
        }
    }

    fun LocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            userLatLng = LatLng(locationResult!!.lastLocation.latitude, locationResult!!.lastLocation.longitude)
            mMap.setMyLocationEnabled(true)
            Log.d("Location","User Lat Long:" +userLatLng.toString())
        }
    }

    private fun addHeatMap() {
        var list: MutableList<LatLng> = arrayListOf()
        // Get the data: latitude/longitude positions of police stations.
        try {
            //TODO  SET THE LIST FROM THE API
            for(i in 0..1000){
//                var lat = Random.nextDouble(-90.0,90.0)
//                var long = Random.nextDouble(-180.0,80.0)
                var lat = Random.nextDouble(19.0,64.0)
                var long = Random.nextDouble(-161.0,-68.0)
                list.add(LatLng(lat,long))
                Log.d("HEATMAP","Lat: "+lat+" Long: "+long+" List: "+list.toString())
            }

        } catch (e: JSONException) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show()
        }
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        heatMapProvider = HeatmapTileProvider.Builder()
            .data(list)
            .build()
        // Add a tile overlay to the map, using the heat map tile provider.
        heatMapOverlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))
    }

    private fun updateHeatMap(){
        // TODO Get updated location data to update heatmap
        var data: ArrayList<LatLng>? = null
        heatMapProvider.setData(data)
        heatMapOverlay.clearTileCache()
    }

}
