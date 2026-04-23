package com.example.musicplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class PlaylistSongAdapter(
    private val songs: List<String>,
    private val songPaths: List<String>,
    private val selectedPaths: ArrayList<String>,
    private val onSelectionChanged: (ArrayList<String>) -> Unit
) : RecyclerView.Adapter<PlaylistSongAdapter.SongViewHolder>() {

    class SongViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val tvSongName: TextView = cardView.findViewById(android.R.id.text1)
        val cbSelect: CheckBox = cardView.findViewById(android.R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val cardView = CardView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60).toInt()
            )
            radius = dp(12).toFloat()
            cardElevation = dp(2).toFloat()
            setCardBackgroundColor(parent.context.getColor(R.color.card_background))
            val margin = dp(4).toInt()
            (layoutParams as ViewGroup.MarginLayoutParams).setMargins(margin, margin, margin, margin)

            val linearLayout = android.widget.LinearLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                orientation = android.widget.LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(dp(16).toInt(), 0, dp(16).toInt(), 0)
            }

            val tvSongName = TextView(parent.context).apply {
                id = android.R.id.text1
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                textSize = 16f
                setTextColor(parent.context.getColor(R.color.text_primary))
                setTypeface(null, android.graphics.Typeface.NORMAL)
            }

            val cbSelect = CheckBox(parent.context).apply {
                id = android.R.id.checkbox
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
                isClickable = false
            }

            linearLayout.addView(tvSongName)
            linearLayout.addView(cbSelect)
            addView(linearLayout)
        }

        return SongViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val songName = songs[position]
        val songPath = songPaths[position]
        holder.tvSongName.text = songName

        val isSelected = selectedPaths.contains(songPath)
        holder.cbSelect.isChecked = isSelected

        holder.cardView.setOnClickListener {
            if (selectedPaths.contains(songPath)) {
                selectedPaths.remove(songPath)
                holder.cbSelect.isChecked = false
            } else {
                selectedPaths.add(songPath)
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

