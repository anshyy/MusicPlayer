package com.example.musicplayer

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.ArrayList

class DailyMixAdapter(
    private val dailyMixes: List<DailyMix>,
    private val onClick: (DailyMix) -> Unit
) : RecyclerView.Adapter<DailyMixAdapter.DailyMixViewHolder>() {

    class DailyMixViewHolder(val cardView: CardView, val titleText: TextView, val descText: TextView, val imageView: ImageView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyMixViewHolder {
        val context = parent.context
        val cardView = CardView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                dpToPx(160),
                dpToPx(160)
            ).apply {
                setMargins(dpToPx(8), 0, dpToPx(8), 0)
            }
            setCardBackgroundColor(context.getColor(R.color.card_background))
            radius = dpToPx(24).toFloat()
            cardElevation = 0f

            val imageView = ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                alpha = 0.6f
            }

            val linearLayout = LinearLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.BOTTOM
                setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            }

            val titleView = TextView(context).apply {
                setTextColor(Color.WHITE)
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val descView = TextView(context).apply {
                setTextColor("#A0A0AB".toColorInt())
                textSize = 12f
                alpha = 0.8f
            }

            addView(imageView)
            linearLayout.addView(titleView)
            linearLayout.addView(descView)
            addView(linearLayout)

            tag = Triple(titleView, descView, imageView)
        }

        @Suppress("UNCHECKED_CAST")
        val tagTriple = cardView.tag as? Triple<TextView, TextView, ImageView>
        val (titleText, descText, imageView) = tagTriple ?: Triple(TextView(context), TextView(context), ImageView(context))
        return DailyMixViewHolder(cardView, titleText, descText, imageView)
    }

    override fun onBindViewHolder(holder: DailyMixViewHolder, position: Int) {
        val mix = dailyMixes[position]
        holder.titleText.text = mix.title
        holder.descText.text = mix.description

        if (mix.imageUri != null) {
            Glide.with(holder.cardView.context)
                .load(mix.imageUri)
                .into(holder.imageView)
            holder.cardView.setCardBackgroundColor(Color.BLACK)
        } else {
            // Use colors from palette for horizontal items if no image
            val colors = listOf("#2D60FF", "#7000FF", "#00D1FF", "#FF2D55")
            try {
                holder.cardView.setCardBackgroundColor(Color.parseColor(colors[position % colors.size]))
            } catch (e: Exception) {
                holder.cardView.setCardBackgroundColor(holder.cardView.context.getColor(R.color.accent_primary))
            }
            holder.imageView.setImageDrawable(null)
        }

        holder.cardView.setOnClickListener { 
            val context = holder.cardView.context
            val intent = Intent(context, ListDetailActivity::class.java).apply {
                putExtra("list_title", mix.title)
                putParcelableArrayListExtra("songs", ArrayList(mix.songs))
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dailyMixes.size

    private fun dpToPx(dp: Int): Int {
        return (dp * 2.5).toInt()
    }
}
