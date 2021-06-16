package com.example.osmdroid.ui.main.view

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.osmdroid.R
import com.example.osmdroid.utils.extentions.isGpsEnabled
import com.example.osmdroid.utils.extentions.isLocationPermissionGranted
import com.example.osmdroid.utils.extentions.requestLocationPermission
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapActivity : AppCompatActivity() {
    private val REQUEST_ACCESS_FINE_LOCATION = 101
    private lateinit var map : MapView
    private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_map)

        setMapConfiguration()
        checkPermissionAndGps()
        //setMyLocation()
    }

    private fun setMapConfiguration() {
        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
    }

    private fun setMapLocation() {
        val mapController = map.controller
        mapController.setZoom(12.0)
        val startPoint = GeoPoint(-3.7815766998816853, 102.26529332970395)
        mapController.setCenter(startPoint)
        // set icon to marked position
        setDestinationMarker()
    }

    private fun setMyLocation() {
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        this.locationOverlay.enableMyLocation()
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

    private fun setDestinationMarker() {
        val destinationGeoPoint = GeoPoint(-3.7815766998816853, 102.26529332970395)
        val destinationMarker = setLocationMarker(destinationGeoPoint)
        map.overlays.add(destinationMarker)
        map.controller.animateTo(destinationGeoPoint)
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
}