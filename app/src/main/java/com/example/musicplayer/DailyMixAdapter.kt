package com.example.musicplayer

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class DailyMixAdapter(
    private val dailyMixes: List<DailyMix>,
    private val onClick: (DailyMix) -> Unit
) : RecyclerView.Adapter<DailyMixAdapter.DailyMixViewHolder>() {

    class DailyMixViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.dailyMixCard)
        val titleView: TextView = view.findViewById(R.id.tvDailyMixTitle)
        val subtitleView: TextView = view.findViewById(R.id.tvDailyMixDesc)
        val songCountView: TextView = view.findViewById(R.id.tvSongCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyMixViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_mix, parent, false)
        return DailyMixViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyMixViewHolder, position: Int) {
        val mix = dailyMixes[position]
        holder.titleView.text = mix.title
        holder.subtitleView.text = mix.description
        holder.songCountView.text = "${mix.songs.size} Songs"

        try {
            holder.cardView.setCardBackgroundColor(Color.parseColor(mix.color))
        } catch (e: Exception) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FA243C"))
        }

        holder.itemView.setOnClickListener { 
            onClick(mix)
        }
    }

    override fun getItemCount() = dailyMixes.size
}
