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
        val artistName: TextView = view.findViewById(R.id.tvArtistName)
        val albumArt: ImageView = view.findViewById(R.id.ivSongArt)
        val btnLike: View = view.findViewById(R.id.btnLike)
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
        
        Glide.with(holder.itemView.context)
            .load(song.albumArtUri)
            .placeholder(R.drawable.gradient_card_pop)
            .error(R.drawable.gradient_card_pop)
            .centerCrop()
            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
            .into(holder.albumArt)

        val isLiked = likedSongPaths.contains(song.path)
        val likeButton = holder.btnLike as? ImageView
        if (isLiked) {
            likeButton?.setColorFilter(androidx.core.content.ContextCompat.getColor(holder.itemView.context, R.color.accent_primary))
        } else {
            likeButton?.setColorFilter(androidx.core.content.ContextCompat.getColor(holder.itemView.context, R.color.text_tertiary))
        }

        holder.itemView.setOnClickListener { 
            it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                onClick(position)
            }.start()
        }
        
        holder.btnLike.setOnClickListener { 
            val newLiked = !isLiked
            onLikeClick(position, newLiked)
            // No need to notifyItemChanged here if MainActivity's listener refreshes the whole adapter
            // but for smooth local animation, we can update just this item
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = songs.size
}