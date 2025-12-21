package com.tobaxiom.placebo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.tobaxiom.placebo.streaksList.StreaksListFragment

// TODO: move material toolbar in both fragments to the main activity xml, then add a back button to it
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StreaksListFragment())
                .commit()
        }
    }
}