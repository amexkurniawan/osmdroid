package com.example.osmdroid.ui.dashboard.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.osmdroid.R
import com.example.osmdroid.ui.main.view.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        onClickAction()
    }

    private fun onClickAction() {
        findViewById<Button>(R.id.btnOsmdroid1).setOnClickListener {
            val intent = Intent(this, OSMBasicActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnOsmdroid2).setOnClickListener {
            val intent = Intent(this, MarkerActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnOsmdroid3).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnOsmdroid4).setOnClickListener {
            val intent = Intent(this, FastOverlayActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnOsmdroid5).setOnClickListener {
            val intent = Intent(this, InfoWindowActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnOsmdroid6).setOnClickListener {
            val intent = Intent(this, PolylinesActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnOsmdroid7).setOnClickListener {
            val intent = Intent(this, PolygonsActivity::class.java)
            startActivity(intent)
        }
    }
}