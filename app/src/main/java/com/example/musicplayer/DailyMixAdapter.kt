package com.example.musicplayer

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView

class DailyMixAdapter(
    private val dailyMixes: List<DailyMix>,
    private val onClick: (DailyMix) -> Unit
) : RecyclerView.Adapter<DailyMixAdapter.DailyMixViewHolder>() {

    class DailyMixViewHolder(val itemView: CardView, val titleText: TextView, val descText: TextView, val songCountText: TextView) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyMixViewHolder {
        val context = parent.context
        val cardView = CardView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(200)
            )
            setCardBackgroundColor("#8B5FBF".toColorInt())
            radius = dpToPx(28).toFloat()
            cardElevation = dpToPx(4).toFloat()
            setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20))

            val linearLayout = LinearLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                orientation = LinearLayout.VERTICAL
            }

            val titleView = TextView(context).apply {
                text = "Daily Mix"
                setTextColor(Color.WHITE)
                textSize = 22f
            }

            val descView = TextView(context).apply {
                text = "Your favorite songs"
                setTextColor("#E0E0E0".toColorInt())
                textSize = 14f
            }

            val countView = TextView(context).apply {
                text = "50 Songs"
                setTextColor("#B0B0B0".toColorInt())
                textSize = 12f
            }

            linearLayout.addView(titleView)
            linearLayout.addView(descView)
            linearLayout.addView(countView)
            addView(linearLayout)

            tag = Triple(titleView, descView, countView)
        }

        @Suppress("UNCHECKED_CAST")
        val tagTriple = cardView.tag as? Triple<TextView, TextView, TextView>
        val (titleText, descText, countText) = tagTriple ?: Triple(TextView(parent.context), TextView(parent.context), TextView(parent.context))
        return DailyMixViewHolder(cardView, titleText, descText, countText)
    }

    override fun onBindViewHolder(holder: DailyMixViewHolder, position: Int) {
        val mix = dailyMixes[position]
        holder.titleText.text = mix.title
        holder.descText.text = mix.description
        holder.songCountText.text = "${mix.songs.size} Songs"

        try {
            val color = mix.color.toColorInt()
            holder.itemView.setCardBackgroundColor(color)
        } catch (e: Exception) {
            holder.itemView.setCardBackgroundColor("#8B5FBF".toColorInt())
        }

        holder.itemView.setOnClickListener { onClick(mix) }
    }

    override fun getItemCount() = dailyMixes.size

    private fun dpToPx(dp: Int): Int {
        return (dp * 2.5).toInt()
    }
}





