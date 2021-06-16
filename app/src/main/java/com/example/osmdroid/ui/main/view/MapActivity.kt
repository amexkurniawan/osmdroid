package com.example.osmdroid.ui.main.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.osmdroid.R
import com.example.osmdroid.utils.extentions.drawableToBitmap
import com.example.osmdroid.utils.extentions.isGpsEnabled
import com.example.osmdroid.utils.extentions.isLocationPermissionGranted
import com.example.osmdroid.utils.extentions.requestLocationPermission
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*


class MapActivity : AppCompatActivity(), LocationListener {
    private val REQUEST_ACCESS_FINE_LOCATION = 101
    private lateinit var map : MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_map)

        setMapConfiguration()
        onClickAction()
        checkPermissionAndGps()
    }

    private fun setMapConfiguration() {
        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        setMyLocation()
    }

    private fun setMapLocation() {
        val mapController = map.controller
        mapController.setZoom(9.5)
        mapController.setCenter(locationOverlay.myLocation)
    }

    private fun setMyLocation() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_my_location)
        val bitmap = drawable?.drawableToBitmap()
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        this.locationOverlay.apply {
            setDirectionArrow(bitmap, bitmap)
            enableMyLocation()
            enableFollowLocation()
        }
        map.overlays.add(locationOverlay)
    }

    private fun setLocationMarker(location: GeoPoint): Marker {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_marker)
        val destinationPoint = GeoPoint(location.latitude, location.longitude)
        val destinationMarker = Marker(map)
        return destinationMarker.apply {
            position = destinationPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = drawable
        }
    }

    private fun setDestinationMarker(lat: Double, long: Double) {
        val destinationGeoPoint = GeoPoint(lat, long)
        val destinationMarker = setLocationMarker(destinationGeoPoint)
        map.overlays.add(destinationMarker)
        map.controller.animateTo(destinationGeoPoint)
    }

    @SuppressLint("MissingPermission")
    private fun getGeoCurrentLocation() {
        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0F, this)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0F, this)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun onClickAction() {
        findViewById<FloatingActionButton>(R.id.fabMyLocation).setOnClickListener {
            map.controller.setZoom(18.0)
            map.controller.animateTo(locationOverlay.myLocation)
        }
        findViewById<FloatingActionButton>(R.id.fabGetCurrentLocation).setOnClickListener {
            getGeoCurrentLocation()
        }
    }

    private fun checkPermissionAndGps() {
        when {
            !isLocationPermissionGranted -> requestLocationPermission(REQUEST_ACCESS_FINE_LOCATION)
            !isGpsEnabled -> {
                Toast.makeText(this, "please enable your gps!", Toast.LENGTH_SHORT).show()
                finish()
            }
            else -> setMapLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        //needed for compass, my location overlays, v6.0.0 and up
        map.onResume()
        checkPermissionAndGps()
    }

    override fun onPause() {
        super.onPause()
        //needed for compass, my location overlays, v6.0.0 and up
        map.onPause()
        locationManager.removeUpdates(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "location permission denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLocationChanged(location: Location) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address: String = addresses[0].getAddressLine(0)

            Log.d("addressss", address)
            Log.d("addressss", "" + location.latitude + "," + location.longitude)
            setDestinationMarker(location.latitude, location.longitude)
            locationManager.removeUpdates(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}