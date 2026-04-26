package com.example.musicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class RecentlyPlayedAdapter(
    private val recentSongs: List<RecentlyPlayed>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<RecentlyPlayedAdapter.RecentlyPlayedViewHolder>() {

    class RecentlyPlayedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.tvSongTitle)
        val albumArt: ImageView = view.findViewById(R.id.ivAlbumArt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyPlayedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recently_played, parent, false)
        return RecentlyPlayedViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentlyPlayedViewHolder, position: Int) {
        val song = recentSongs[position]
        holder.songName.text = song.songName
        
        Glide.with(holder.itemView.context)
            .load(song.albumArtUri)
            .placeholder(R.drawable.gradient_card_pop)
            .error(R.drawable.gradient_card_pop)
            .centerCrop()
            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
            .into(holder.albumArt)

        holder.itemView.setOnClickListener { 
            it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                onClick(position)
            }.start()
        }
    }

    override fun getItemCount() = recentSongs.size

    private fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000} min ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
