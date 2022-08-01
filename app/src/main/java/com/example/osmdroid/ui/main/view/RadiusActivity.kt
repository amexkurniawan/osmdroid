package com.example.osmdroid.ui.main.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

class RadiusActivity : AppCompatActivity(), LocationListener {

    private val REQUEST_ACCESS_FINE_LOCATION = 101
    private lateinit var map : MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var locationManager: LocationManager
    private val destination = GeoPoint(-3.7558874657289567, 102.27236375159629)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_radius)

        setMapConfiguration()
        onClickAction()
        checkPermissionAndGps()
    }

    private fun setMapConfiguration() {
        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        setMyLocation()
        setMarkerLocation(destination, 1)
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

    private fun setMapLocation() {
        val mapController = map.controller
        mapController.setZoom(9.5)
        mapController.setCenter(locationOverlay.myLocation)
    }

    private fun setMarkerLocation(location: GeoPoint, markerNo: Int) {
        val marker = Marker(map)
        val geoPoint = GeoPoint(location.latitude, location.longitude)
        marker.position = geoPoint
        marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_marker)
        marker.title = "Marker ${markerNo}"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(marker)
        map.invalidate()
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

    private fun getDistanceInMeters(myLocation: Location): Float {
        val myDestination = Location("")
        myDestination.latitude = destination.latitude
        myDestination.longitude = destination.longitude

        return myLocation.distanceTo(myDestination)
    }

    private fun onClickAction() {
        findViewById<FloatingActionButton>(R.id.fabMyLocation).setOnClickListener {
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
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "location permission denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLocationChanged(location: Location) {
        try {
            val distanceInMeters = getDistanceInMeters(location) / 1000
            val rounded = String.format("%.2f", distanceInMeters)
            Toast.makeText(this, "$rounded KM", Toast.LENGTH_SHORT).show()
            locationManager.removeUpdates(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}