package com.example.musicplayer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DailyMixAdapter(
    private val dailyMixes: List<DailyMix>,
    private val onClick: (DailyMix) -> Unit
) : RecyclerView.Adapter<DailyMixAdapter.DailyMixViewHolder>() {

    class DailyMixViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.dailyMixCard)
        val titleView: TextView = view.findViewById(R.id.tvDailyMixTitle)
        val subtitleView: TextView = view.findViewById(R.id.tvDailyMixDesc)
        val songCountView: TextView = view.findViewById(R.id.tvSongCount)
        val imageView: ImageView = view.findViewById(R.id.ivDailyMixArt)
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

        if (mix.imageUri != null) {
            Glide.with(holder.itemView.context)
                .load(mix.imageUri)
                .centerCrop()
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                .into(holder.imageView)
        } else {
            val fallback = VisualUtils.getPremiumImageForSeed(mix.title)
            Glide.with(holder.itemView.context)
                .load(fallback)
                .centerCrop()
                .into(holder.imageView)
        }

        holder.itemView.setOnClickListener { 
            it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                onClick(mix)
            }.start()
        }
    }

    override fun getItemCount() = dailyMixes.size
}
