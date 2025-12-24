package com.tobaxiom.placebo.streaksList

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tobaxiom.placebo.R
import com.tobaxiom.placebo.Streak
import com.tobaxiom.placebo.streakView.StreakViewFragment
import com.tobaxiom.placebo.streaksList.StreaksRVAdapter

class StreaksListFragment: Fragment(R.layout.streaks_list_view) {
    lateinit var addButton: FloatingActionButton
    lateinit var streaksRV: RecyclerView
    lateinit var tvNoStreaks: TextView

    var streaks = mutableListOf<Streak>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addButton = view.findViewById(R.id.addStreakButton)
        streaksRV = view.findViewById(R.id.rvStreaksList)
        tvNoStreaks = view.findViewById(R.id.tvNoStreaks)

        if (streaks.isEmpty()) {
            streaksRV.visibility = View.GONE
            tvNoStreaks.visibility = View.VISIBLE
        } else {
            streaksRV.visibility = View.VISIBLE
            tvNoStreaks.visibility = View.GONE
        }

        streaksRV.layoutManager = LinearLayoutManager(requireContext())
        streaksRV.adapter = StreaksRVAdapter(streaks, this::navigateToStreakView, this::editStreak, this::removeStreak)

        addButton.setOnClickListener { view ->
            showAddStreakDialog()
        }
    }

    fun navigateToStreakView(streak: Streak) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StreakViewFragment(streak))
            .addToBackStack(null)
            .commit()

        (requireActivity() as AppCompatActivity)
            .supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun removeStreak(streak: Streak) {
        val streakPosition = streaks.indexOf(streak)
        streaks.remove(streak)

        streaksRV.adapter?.notifyItemRemoved(streakPosition)

        if (!streaksRV.isVisible) {
            streaksRV.visibility = View.VISIBLE
            tvNoStreaks.visibility = View.GONE
        }
    }

    fun editStreak(streak: Streak) {
        val dialogView = layoutInflater.inflate(R.layout.new_streak_dialog, null)
        val etStreakName = dialogView.findViewById<EditText>(R.id.etStreakName)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit streak")
            .setView(dialogView)
            .setPositiveButton("Set Name") {_, _ ->
                val streakName = etStreakName.text.toString().trim()
                if (streakName.isNotEmpty()) {
                    streak.name = streakName
                    streaksRV.adapter?.notifyItemChanged(streaks.indexOf(streak))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun showAddStreakDialog() {
        val dialogView = layoutInflater.inflate(R.layout.new_streak_dialog, null)
        val etStreakName = dialogView.findViewById<EditText>(R.id.etStreakName)

        AlertDialog.Builder(requireContext())
            .setTitle("Add a new streak")
            .setView(dialogView)
            .setPositiveButton("Add") {_, _ ->
                val streakName = etStreakName.text.toString().trim()
                if (streakName.isNotEmpty()) {
                    addNewStreak(streakName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun addNewStreak(name: String) {
        streaks.add(Streak(name))
        streaksRV.adapter?.notifyItemInserted(streaks.size - 1)

        if (!streaksRV.isVisible) {
            streaksRV.visibility = View.VISIBLE
            tvNoStreaks.visibility = View.GONE
        }
    }
}