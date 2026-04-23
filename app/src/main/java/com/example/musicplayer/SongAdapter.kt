package com.example.musicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SongAdapter(
    private val songs: List<Song>,
    private val likedSongPaths: HashSet<String>,
    private val onClick: (Int) -> Unit,
    private val onLikeClick: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.tvSongName)
        val songNumber: TextView = view.findViewById(R.id.tvSongNumber)
        val artistName: TextView = view.findViewById(R.id.tvArtistName)
        val albumArt: ImageView = view.findViewById(R.id.ivSongArt)
        val btnLike: View = view.findViewById(R.id.btnLike) // Now hidden but kept for logic
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songName.text = song.title
        holder.artistName.text = song.artist
        holder.songNumber.text = String.format("%02d", position + 1)
        
        Glide.with(holder.itemView.context)
            .load(song.albumArtUri)
            .placeholder(R.drawable.gradient_card_pop)
            .error(R.drawable.gradient_card_pop)
            .into(holder.albumArt)

        val isLiked = likedSongPaths.contains(song.path)
        // Color filter logic can be removed or moved to the 'More' button if desired
        // For now, let's keep it simple as per the new UI design which uses a 'More' menu button instead of Like in the list


        holder.itemView.setOnClickListener { onClick(position) }
        
        holder.btnLike.setOnClickListener { 
            val newLiked = !isLiked
            onLikeClick(position, newLiked)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = songs.size
}