package com.example.osmdroid.ui.dashboard.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.osmdroid.R
import com.example.osmdroid.ui.main.view.MainActivity
import com.example.osmdroid.ui.main.view.MapActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        onClickAction()
    }

    private fun onClickAction() {
        findViewById<Button>(R.id.btnOsmdroid1).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnOsmdroid2).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }
}