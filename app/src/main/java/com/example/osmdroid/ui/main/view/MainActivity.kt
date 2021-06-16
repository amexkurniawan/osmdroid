package com.example.osmdroid.ui.main.view

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.osmdroid.R
import com.example.osmdroid.utils.extentions.isGpsEnabled
import com.example.osmdroid.utils.extentions.isLocationPermissionGranted
import com.example.osmdroid.utils.extentions.requestLocationPermission
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MinimapOverlay
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {

    private val REQUEST_ACCESS_FINE_LOCATION = 101
    private lateinit var map : MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var compassOverlay: CompassOverlay
    private lateinit var scaleBarOverlay: ScaleBarOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_main)

        setMapConfiguration()
        checkPermissionAndGps()
        setMyLocation()
    }

    private fun setMapConfiguration() {
        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        //setCompassOverlay()
        //setGridLine()
        setRotationGesture()
        setMapScaleBar()
        //setBuildinMiniMap()
    }

    private fun setMapLocation() {
        val mapController = map.controller
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(-3.7815766998816853, 102.26529332970395)
        mapController.setCenter(startPoint)
    }

    private fun setMyLocation() {
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        this.locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)
    }

    private fun setCompassOverlay() {
        compassOverlay = CompassOverlay(this, InternalCompassOrientationProvider(this), map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)
    }

    private fun setGridLine() {
        val overlay = LatLonGridlineOverlay2()
        map.overlays.add(overlay)
    }

    private fun setRotationGesture() {
        val rotationGestureOverlay = RotationGestureOverlay(map)
        rotationGestureOverlay.isEnabled
        map.setMultiTouchControls(true)
        map.overlays.add(rotationGestureOverlay)
    }

    private fun setMapScaleBar() {
        val displayMetrics = this.resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(map)
        scaleBarOverlay.setCentred(true)
        //play around with these values to get the location on screen in the right place for your application
        scaleBarOverlay.setScaleBarOffset(displayMetrics.widthPixels / 2, 10)
        map.overlays.add(scaleBarOverlay)
    }

    private fun setBuildinMiniMap() {
        val displayMetrics = this.resources.displayMetrics
        val minimapOverlay = MinimapOverlay(this, map.tileRequestCompleteHandler)
        minimapOverlay.setWidth(displayMetrics.widthPixels / 5)
        minimapOverlay.setHeight(displayMetrics.heightPixels / 5)
        //optionally, you can set the minimap to a different tile source
        //minimapOverlay.setTileSource(....)
        map.overlays.add(minimapOverlay)
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