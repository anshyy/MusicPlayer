package com.example.musicplayer

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class PlaylistAdapter(
    private val playlists: List<Playlist>,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlaylist: ImageView = view.findViewById(R.id.ivPlaylistThumb)
        val tvName: TextView = view.findViewById(R.id.tvPlaylistName)
        val tvSub: TextView = view.findViewById(R.id.tvPlaylistSub)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.tvName.text = playlist.name
        val songCount = playlist.songPaths.size
        holder.tvSub.text = "$songCount ${if (songCount == 1) "song" else "songs"}"
        if (playlist.imageUri != null) {
            holder.ivPlaylist.setImageURI(playlist.imageUri)
        } else {
            holder.ivPlaylist.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        
        holder.itemView.setOnClickListener { onClick(playlist) }
    }

    override fun getItemCount() = playlists.size
}
