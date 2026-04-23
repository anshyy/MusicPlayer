package com.example.musicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(
    private val songs: List<String>,
    private val songPaths: List<String>,
    private val likedSongPaths: HashSet<String>,
    private val onClick: (Int) -> Unit,
    private val onLikeClick: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.tvSongName)
        val btnLike: ImageButton = view.findViewById(R.id.btnLike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.songName.text = songs[position]
        
        // Use the new heart icon
        holder.btnLike.setImageResource(R.drawable.ic_heart)
        
        val isLiked = likedSongPaths.contains(songPaths[position])
        if (isLiked) {
            holder.btnLike.setColorFilter(android.graphics.Color.parseColor("#FF69B4")) // Pink for liked
        } else {
            holder.btnLike.setColorFilter(android.graphics.Color.parseColor("#94A3B8")) // text_tertiary
        }

        holder.itemView.setOnClickListener { onClick(position) }
        
        holder.btnLike.setOnClickListener { 
            val newLiked = !isLiked
            onLikeClick(position, newLiked)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = songs.size
}