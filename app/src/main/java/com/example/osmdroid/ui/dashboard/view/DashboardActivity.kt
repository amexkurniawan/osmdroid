package com.example.osmdroid.ui.dashboard.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.osmdroid.R
import com.example.osmdroid.ui.main.view.FastOverlayActivity
import com.example.osmdroid.ui.main.view.OSMBasicActivity
import com.example.osmdroid.ui.main.view.MarkerActivity
import com.example.osmdroid.ui.main.view.MapActivity

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
    }
}