package com.example.osmdroid.ui.main.view

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.osmdroid.R
import com.example.osmdroid.utils.extentions.isGpsEnabled
import com.example.osmdroid.utils.extentions.isLocationPermissionGranted
import com.example.osmdroid.utils.extentions.requestLocationPermission
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme


class FastOverlayActivity : AppCompatActivity() {

    private val REQUEST_ACCESS_FINE_LOCATION = 101
    private lateinit var map : MapView
    private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_fast_overlay)

        setMapConfiguration()
        checkPermissionAndGps()
    }

    private fun setMapConfiguration() {
        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        setRotationGesture()
        setMapScaleBar()
    }

    private fun setMapLocation() {
        val mapController = map.controller
        mapController.setZoom(8.0)
        val startPoint = GeoPoint(-3 + Math.random() * 5, 102 + Math.random() * 5)
        setMarkerFastOverlay()
        mapController.setCenter(startPoint)
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

    private fun setMarkerFastOverlay() {
        // can create up to 10k labelled points
        val points = ArrayList<IGeoPoint>()
        for (i in 0..99) {
            points.add(
                LabelledGeoPoint(
                    -3 + Math.random() * 5,
                    102 + Math.random() * 5,
                    "Point #$i")) }

        // wrap them in a theme
        val pt = SimplePointTheme(points, true)

        // create label style
        val textStyle = Paint()
        textStyle.style = Paint.Style.FILL
        textStyle.color = Color.parseColor("#FF0000")
        textStyle.textAlign = Paint.Align.CENTER
        textStyle.textSize = 24F

        // create point style
        val pointStyle = Paint()
        pointStyle.style = Paint.Style.FILL
        pointStyle.color = Color.parseColor("#25AF6F")

        // set some visual options for the overlay
        // we use here MAXIMUM_OPTIMIZATION algorithm, which works well with >100k points
        val opt = SimpleFastPointOverlayOptions.getDefaultStyle()
            .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
            .setRadius(7F).setIsClickable(true).setCellSize(15).setTextStyle(textStyle).setPointStyle(pointStyle)

        // create the overlay with the theme
        val sfpo = SimpleFastPointOverlay(pt, opt)

        // onClick callback
        sfpo.setOnClickListener { _, point ->
            Toast.makeText(this, "You clicked " + (points.get(point) as LabelledGeoPoint).getLabel(), Toast.LENGTH_SHORT).show()
        }

        map.overlays.add(sfpo);
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
}