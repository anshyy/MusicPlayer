package com.example.musicplayer

import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class RecentlyPlayedAdapter(
    private val recentSongs: List<RecentlyPlayed>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<RecentlyPlayedAdapter.RecentlyPlayedViewHolder>() {

    class RecentlyPlayedViewHolder(itemView: CardView, val songName: TextView, val playedTime: TextView) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyPlayedViewHolder {
        val context = parent.context
        val cardView = CardView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(72)
            )
            setCardBackgroundColor("#1E1E2E".toColorInt())
            radius = dpToPx(16).toFloat()
            cardElevation = dpToPx(2).toFloat()

            val relLayout = LinearLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                orientation = LinearLayout.HORIZONTAL
                setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
            }

            val textLayout = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                orientation = LinearLayout.VERTICAL
            }

            val songNameView = TextView(context).apply {
                setTextColor(Color.WHITE)
                textSize = 16f
                isSingleLine = true
                ellipsize = android.text.TextUtils.TruncateAt.END
            }

            val timeView = TextView(context).apply {
                setTextColor("#909090".toColorInt())
                textSize = 12f
            }

            textLayout.addView(songNameView)
            textLayout.addView(timeView)
            relLayout.addView(textLayout)
            addView(relLayout)

            // Store references for ViewHolder
            tag = Pair(songNameView, timeView)
        }

        @Suppress("UNCHECKED_CAST")
        val tagPair = cardView.tag as? Pair<TextView, TextView>
        val (songNameView, timeView) = tagPair ?: Pair(TextView(parent.context), TextView(parent.context))
        return RecentlyPlayedViewHolder(cardView, songNameView, timeView)
    }

    override fun onBindViewHolder(holder: RecentlyPlayedViewHolder, position: Int) {
        val song = recentSongs[position]
        holder.songName.text = song.songName
        holder.playedTime.text = formatTime(song.playedAt)
        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun getItemCount() = recentSongs.size

    private fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000} min ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            diff < 604800000 -> "${diff / 86400000}d ago"
            else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * 2.5).toInt()
    }
}





