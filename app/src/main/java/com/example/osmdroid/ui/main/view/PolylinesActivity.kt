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
import org.osmdroid.views.overlay.Polyline

class PolylinesActivity : AppCompatActivity() {

    private val REQUEST_ACCESS_FINE_LOCATION = 101
    private lateinit var map : MapView
    private val point1 = GeoPoint(-3.8064013093232782, 102.28435055622985)
    private val point2 = GeoPoint(-3.825070920757973, 102.29602352876405)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_polylines)

        setMapConfiguration()
        checkPermissionAndGps()
    }

    private fun setMapConfiguration() {
        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
    }

    private fun setMapLocation() {
        val mapController = map.controller
        mapController.setZoom(12.0)
        val startPoint = GeoPoint(-3.7815766998816853, 102.26529332970395)
        setMarkerLocation(point1)
        setMarkerLocation(point2)
        setPolylines()
        mapController.setCenter(startPoint)
    }

    private fun setMarkerLocation(location: GeoPoint) {
        val marker = Marker(map)
        val geoPoint = GeoPoint(location.latitude, location.longitude)
        marker.position = geoPoint
        marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_marker)
        marker.title = "Test Marker"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(marker)
        map.invalidate()
    }

    private fun setPolylines() {
        val geoPoints = ArrayList<GeoPoint>()
        geoPoints.add(point1)
        geoPoints.add(point2)
        //add your points here
        val line = Polyline()
        line.setPoints(geoPoints)
        line.setOnClickListener({ polyline, mapView, eventPos ->
            Toast.makeText(mapView.context, "polyline with " + line.actualPoints.size + " pts was tapped", Toast.LENGTH_LONG).show()
            false
        })
        map.overlays.add(line)
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