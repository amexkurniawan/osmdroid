package com.example.osmdroid.utils.library

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.osmdroid.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class MarkerWindow(mapView: MapView, private val lat: String, private val long: String): InfoWindow(R.layout.layout_custom_info_windows, mapView) {
    override fun onOpen(item: Any?) {
        // Following command
        closeAllInfoWindowsOn(mapView)
        // Here we are settings onclick listeners for the buttons in the layouts.
        val latitudeText = mView.findViewById<TextView>(R.id.tvLatitude)
        val longtitudeText = mView.findViewById<TextView>(R.id.tvLongtitude)
        val moveButton = mView.findViewById<Button>(R.id.btnMove)
        val deleteButton = mView.findViewById<Button>(R.id.btnDelete)

        latitudeText.text = lat
        longtitudeText.text = long

        moveButton.setOnClickListener {
            /*  How to create a moveMarkerMapListener is not covered here.
                Use the Map Listeners guide for this instead */
            //mapView.addMapListener(MoveMarkerMapListener)
            Toast.makeText(mapView.context, "Move", Toast.LENGTH_SHORT).show()
        }

        deleteButton.setOnClickListener {
             /* Do Something
                In order to delete the marker,
                You would probably have to pass the "map class"
                where the map was created,
                along with an ID to reference the marker.
                Using a HashMap to store markers would be useful here
                so that the markers can be referenced by ID.
                Once you get the marker,
                you would do map.overlays.remove(marker) */
            Toast.makeText(mapView.context, "Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClose() {
        // Do something
    }

}