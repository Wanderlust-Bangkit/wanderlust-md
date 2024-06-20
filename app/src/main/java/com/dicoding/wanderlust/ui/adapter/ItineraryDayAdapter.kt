package com.dicoding.wanderlust.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.databinding.ItemItineraryDayBinding
import com.dicoding.wanderlust.remote.response.PlanItem

class ItineraryDayAdapter(private val planItems: List<PlanItem>) : RecyclerView.Adapter<ItineraryDayAdapter.ItineraryDayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryDayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemItineraryDayBinding.inflate(inflater, parent, false)
        return ItineraryDayViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return planItems.size
    }

    override fun onBindViewHolder(holder: ItineraryDayViewHolder, position: Int) {
        val planItem = planItems[position]
        holder.bind(planItem)
    }

    inner class ItineraryDayViewHolder(private val binding: ItemItineraryDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(planItem: PlanItem) {
            binding.tvDay.text = itemView.context.getString(R.string.day_count, planItem.day ?: 0)

            val destinationAdapter = DestinationAdapter { _ ->
                // Handle item click here
            }

            binding.rvDestination.adapter = destinationAdapter
            destinationAdapter.submitList(planItem.destinations ?: emptyList())
        }
    }

}
