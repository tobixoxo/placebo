package com.tobaxiom.placebo.streakView

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.tobaxiom.placebo.R
import com.tobaxiom.placebo.Streak
import java.time.LocalDate

class StreakViewFragment(val streak: Streak): Fragment(R.layout.streak_view) {
    lateinit var heading: TextView
    lateinit var tvCurrentStreakValue: TextView
    lateinit var tvLongestStreakValue: TextView
    lateinit var tbMarkToday: ToggleButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        heading = view.findViewById(R.id.heading)
        tvCurrentStreakValue = view.findViewById(R.id.tvCurrentStreakValue)
        tvLongestStreakValue = view.findViewById(R.id.tvLongestStreakValue)
        tbMarkToday = view.findViewById(R.id.tbMarkToday)

        heading.text = streak.name
        tvLongestStreakValue.text = streak.getLongestStreak().toString()
        tvCurrentStreakValue.text = streak.getCurrentStreak().toString()

        tbMarkToday.setOnClickListener { view ->
            if (LocalDate.now() !in streak.markedDays) {
                streak.markForToday()
            } else {
                streak.unmarkForToday()
            }

            tvLongestStreakValue.text = streak.getLongestStreak().toString()
            tvCurrentStreakValue.text = streak.getCurrentStreak().toString()

            Log.i("abc", streak.markedDays.toString())
        }
    }
}