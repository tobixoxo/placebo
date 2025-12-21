package com.tobaxiom.placebo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StreaksRVAdapter(val streaks: MutableList<Streak>): RecyclerView.Adapter<StreaksRVAdapter.StreakRVItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StreakRVItemViewHolder {
        Log.i("adapter", "on create")
        val view = LayoutInflater.from(parent.getContext()).inflate(R.layout.streak_rv_item, parent, false)
        return StreakRVItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StreakRVItemViewHolder,
        position: Int
    ) {
        Log.i("adapter", "on bind")
        holder.streakName.text = streaks[position].name
    }

    override fun getItemCount(): Int {
        Log.i("adapter", "get item count")
        return streaks.size
    }

    class StreakRVItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val streakName: TextView = itemView.findViewById(R.id.tvStreakName)
    }
}