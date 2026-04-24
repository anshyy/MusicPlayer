package com.example.musicplayer

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.ArrayList

class DailyMixAdapter(
    private val dailyMixes: List<DailyMix>,
    private val onClick: (DailyMix) -> Unit
) : RecyclerView.Adapter<DailyMixAdapter.DailyMixViewHolder>() {

    class DailyMixViewHolder(val container: View, val imageView: ImageView, val titleView: TextView, val subtitleView: TextView) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyMixViewHolder {
        val context = parent.context
        
        // Vertical layout to hold card and labels below it
        val root = LinearLayout(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                dpToPx(180),
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dpToPx(8), 0, dpToPx(8), 0)
            }
            orientation = LinearLayout.VERTICAL
        }

        val cardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(180)
            )
            radius = dpToPx(8).toFloat()
            cardElevation = 4f
        }

        val imageView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        cardView.addView(imageView)

        val titleView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dpToPx(8)
            }
            setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_primary))
            textSize = 15f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val subtitleView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_secondary))
            textSize = 13f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        root.addView(cardView)
        root.addView(titleView)
        root.addView(subtitleView)

        return DailyMixViewHolder(root, imageView, titleView, subtitleView)
    }

    override fun onBindViewHolder(holder: DailyMixViewHolder, position: Int) {
        val mix = dailyMixes[position]
        holder.titleView.text = mix.title
        holder.subtitleView.text = mix.description

        if (mix.imageUri != null) {
            Glide.with(holder.itemView.context)
                .load(mix.imageUri)
                .placeholder(R.drawable.gradient_card_pop)
                .into(holder.imageView)
        } else {
            val colors = listOf("#2D60FF", "#7000FF", "#00D1FF", "#FF2D55")
            holder.imageView.setBackgroundColor(Color.parseColor(colors[position % colors.size]))
            holder.imageView.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener { 
            val context = holder.itemView.context
            val intent = Intent(context, ListDetailActivity::class.java).apply {
                putExtra("list_title", mix.title)
                putStringArrayListExtra("song_names", ArrayList(mix.songs))
                putStringArrayListExtra("song_paths", ArrayList(mix.songPaths))
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dailyMixes.size

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = android.content.res.Resources.getSystem().displayMetrics
        return (dp * displayMetrics.density).toInt()
    }
}
