package com.example.musicplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class PlaylistSongAdapter(
    private val songs: List<Song>,
    private val selectedPaths: ArrayList<String>,
    private val onSelectionChanged: (ArrayList<String>) -> Unit
) : RecyclerView.Adapter<PlaylistSongAdapter.SongViewHolder>() {

    class SongViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val tvSongName: TextView = cardView.findViewById(android.R.id.text1)
        val tvArtistName: TextView = cardView.findViewById(android.R.id.text2)
        val cbSelect: CheckBox = cardView.findViewById(android.R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val cardView = CardView(parent.context).apply {
            val margin = dp(4).toInt()
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(72).toInt()
            ).apply {
                setMargins(margin, margin, margin, margin)
            }
            radius = dp(12).toFloat()
            cardElevation = dp(2).toFloat()
            setCardBackgroundColor(parent.context.getColor(R.color.card_background))

            val linearLayout = android.widget.LinearLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                orientation = android.widget.LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(dp(16).toInt(), 0, dp(16).toInt(), 0)
            }

            val textLayout = android.widget.LinearLayout(parent.context).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                orientation = android.widget.LinearLayout.VERTICAL
            }

            val tvSongName = TextView(parent.context).apply {
                id = android.R.id.text1
                textSize = 16f
                setTextColor(parent.context.getColor(R.color.text_primary))
                setTypeface(null, android.graphics.Typeface.BOLD)
                isSingleLine = true
                android.text.TextUtils.TruncateAt.END
            }

            val tvArtistName = TextView(parent.context).apply {
                id = android.R.id.text2
                textSize = 12f
                setTextColor(parent.context.getColor(R.color.text_tertiary))
            }

            textLayout.addView(tvSongName)
            textLayout.addView(tvArtistName)

            val cbSelect = CheckBox(parent.context).apply {
                id = android.R.id.checkbox
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
                isClickable = false
            }

            linearLayout.addView(textLayout)
            linearLayout.addView(cbSelect)
            addView(linearLayout)
        }

        return SongViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.tvSongName.text = song.title
        holder.tvArtistName.text = song.artist

        val isSelected = selectedPaths.contains(song.path)
        holder.cbSelect.isChecked = isSelected

        holder.cardView.setOnClickListener {
            if (selectedPaths.contains(song.path)) {
                selectedPaths.remove(song.path)
                holder.cbSelect.isChecked = false
            } else {
                selectedPaths.add(song.path)
                holder.cbSelect.isChecked = true
            }
            onSelectionChanged(ArrayList(selectedPaths)) // Create a copy to ensure updates
        }
    }

    override fun getItemCount() = songs.size

    private fun dp(value: Int): Float {
        return (value * 2.5).toFloat()
    }
}

