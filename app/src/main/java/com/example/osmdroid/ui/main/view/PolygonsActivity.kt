package com.example.osmdroid.ui.main.view

import android.content.pm.PackageManager
import android.graphics.Color
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
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline

class PolygonsActivity : AppCompatActivity() {

    private val REQUEST_ACCESS_FINE_LOCATION = 101
    private lateinit var map : MapView
    private val point1 = GeoPoint(-3.7912098005069663, 102.27572030412874)
    private val point2 = GeoPoint(-3.789671705410748, 102.31502771065189)
    private val point3 = GeoPoint(-3.8285077662966533, 102.33429604718285)
    private val point4 = GeoPoint(-3.8311993094084293, 102.30038377488837)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_polygons)

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
        setMarkerLocation(point3)
        setMarkerLocation(point4)
        setPolygon()
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

    private fun setPolygon() {
        val geoPoints = ArrayList<GeoPoint>()
        val polygon = Polygon()

        geoPoints.add(point1)
        geoPoints.add(point2)
        geoPoints.add(point3)
        geoPoints.add(point4)
        geoPoints.add(geoPoints.get(0))

        polygon.fillPaint.color = Color.parseColor("#1EFFE70E")
        polygon.title = "A sample polygon"
        polygon.setPoints(geoPoints)

        /*
        //polygons supports holes too, points should be in a counter-clockwise order
        val holes = ArrayList<ArrayList<GeoPoint>>()
        // Note, you will have to create "moreGeoPoints" yourself.
        holes.add(moreGeoPoints)
        polygon.setHoles(holes) */

        map.overlays.add(polygon)
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