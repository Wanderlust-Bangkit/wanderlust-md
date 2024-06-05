package com.dicoding.wanderlust.ui.boarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.wanderlust.R

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
            textTitle.text = onboardingItem.title
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
