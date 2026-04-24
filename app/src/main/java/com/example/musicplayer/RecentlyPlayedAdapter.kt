package com.example.musicplayer

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class RecentlyPlayedAdapter(
    private val recentSongs: List<RecentlyPlayed>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<RecentlyPlayedAdapter.RecentlyPlayedViewHolder>() {

    class RecentlyPlayedViewHolder(itemView: android.view.View, val songName: TextView, val playedTime: TextView, val albumArt: ImageView) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyPlayedViewHolder {
        val context = parent.context
        
        val linearLayout = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(dpToPx(24), dpToPx(8), dpToPx(24), dpToPx(8))
            gravity = android.view.Gravity.CENTER_VERTICAL
            
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            val typedArray = context.obtainStyledAttributes(attrs)
            background = typedArray.getDrawable(0)
            typedArray.recycle()
            
            isClickable = true
            isFocusable = true
        }

        val cardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(48), dpToPx(48))
            radius = dpToPx(4).toFloat()
            cardElevation = 2f
        }

        val albumArtView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        cardView.addView(albumArtView)

        val textLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(12), 0, 0, 0)
        }

        val songNameView = TextView(context).apply {
            setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_primary))
            textSize = 16f
            isSingleLine = true
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val timeView = TextView(context).apply {
            setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_secondary))
            textSize = 12f
        }

        linearLayout.addView(cardView)
        textLayout.addView(songNameView)
        textLayout.addView(timeView)
        linearLayout.addView(textLayout)

        return RecentlyPlayedViewHolder(linearLayout, songNameView, timeView, albumArtView)
    }

    override fun onBindViewHolder(holder: RecentlyPlayedViewHolder, position: Int) {
        val song = recentSongs[position]
        holder.songName.text = song.songName
        holder.playedTime.text = formatTime(song.playedAt)
        
        Glide.with(holder.itemView.context)
            .load(song.albumArtUri)
            .placeholder(R.drawable.gradient_card_pop)
            .into(holder.albumArt)

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
            else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = android.content.res.Resources.getSystem().displayMetrics
        return (dp * displayMetrics.density).toInt()
    }
}
