package com.dicoding.wanderlust.ui.boarding

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.wanderlust.R
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat


data class BoardingItem(
    val imageResId: Int,
    val title: String,
    val description: String
)

class BoardingAdapter(private val onboardingItems: List<BoardingItem>) :
    RecyclerView.Adapter<BoardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageOnboarding: ImageView = view.findViewById(R.id.imageOnboarding)
        private val textTitle: TextView = view.findViewById(R.id.textTitle)
        private val textDescription: TextView = view.findViewById(R.id.textDescription)

        fun bind(onboardingItem: BoardingItem) {
            imageOnboarding.setImageResource(onboardingItem.imageResId)

            // Create a SpannableString with the title text
            val spannableTitle = SpannableString(onboardingItem.title)

            // Find the index of the first space in the title (assuming the first word ends at the first space)
            val firstSpaceIndex = onboardingItem.title.indexOf(' ')

            // Apply a ForegroundColorSpan to the first word (from start to the first space)
            if (firstSpaceIndex != -1) {
                spannableTitle.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.md_theme_errorContainer_mediumContrast)),
                    0, // start index of the span
                    firstSpaceIndex, // end index of the span (exclusive)
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            // Set the SpannableString to the textTitle TextView
            textTitle.text = spannableTitle

            textDescription.text = onboardingItem.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.onboarding_item_container, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount() = onboardingItems.size
}
