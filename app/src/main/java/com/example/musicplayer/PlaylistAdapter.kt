package com.example.musicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaylistAdapter(
    private val playlists: List<Playlist>,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlaylist: ImageView = view.findViewById(R.id.ivPlaylistThumb)
        val tvName: TextView = view.findViewById(R.id.tvPlaylistName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.tvName.text = playlist.name
        if (playlist.imageUri != null) {
            holder.ivPlaylist.setImageURI(playlist.imageUri)
        } else {
            holder.ivPlaylist.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        holder.itemView.setOnClickListener { onClick(playlist) }
    }

    override fun getItemCount() = playlists.size
}
