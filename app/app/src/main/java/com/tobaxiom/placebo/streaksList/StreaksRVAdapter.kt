package com.tobaxiom.placebo.streaksList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobaxiom.placebo.R
import com.tobaxiom.placebo.Streak

class StreaksRVAdapter(
    val streaks: MutableList<Streak>,
    val streakOnClickListener: (Streak) -> Unit,
    val streakEditListener: (Streak) -> Unit,
    val streakRemoveListener: (Streak) -> Unit
): RecyclerView.Adapter<StreaksRVAdapter.StreakRVItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StreakRVItemViewHolder {
        val view = LayoutInflater.from(parent.getContext()).inflate(R.layout.streak_rv_item, parent, false)
        return StreakRVItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StreakRVItemViewHolder,
        position: Int
    ) {
        val streak = streaks[position]
        holder.streakName.text = streak.name
        holder.setOnClickListener(streak)
    }

    override fun getItemCount(): Int {
        return streaks.size
    }

    inner class StreakRVItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val streakName: TextView = itemView.findViewById(R.id.tvStreakName)
        val editButton = itemView.findViewById<ImageView>(R.id.ivEdit)
        val removeButton = itemView.findViewById<ImageView>(R.id.ivDelete)

        fun setOnClickListener(streak: Streak) {
            itemView.setOnClickListener { streakOnClickListener(streak) }
            editButton.setOnClickListener { streakEditListener(streak) }
            removeButton.setOnClickListener { streakRemoveListener(streak) }
        }
    }
}