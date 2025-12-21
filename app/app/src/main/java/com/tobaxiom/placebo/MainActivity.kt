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

class MainActivity : AppCompatActivity() {
    lateinit var addButton: FloatingActionButton
    lateinit var streaksRV: RecyclerView
    lateinit var tvNoStreaks: TextView

    var streaks = mutableListOf<Streak>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        addButton = findViewById(R.id.addStreakButton)
        streaksRV = findViewById(R.id.rvStreaksList)
        tvNoStreaks = findViewById(R.id.tvNoStreaks)

        streaksRV.layoutManager = LinearLayoutManager(this)
        streaksRV.adapter = StreaksRVAdapter(streaks)

        addButton.setOnClickListener { view ->
            streaks.add(Streak("abcd"))
            streaksRV.adapter?.notifyItemInserted(streaks.size - 1)
            Log.i("main", "clicked add")

            if (!streaksRV.isVisible) {
                streaksRV.visibility = View.VISIBLE
                tvNoStreaks.visibility = View.GONE
            }
        }
    }
}