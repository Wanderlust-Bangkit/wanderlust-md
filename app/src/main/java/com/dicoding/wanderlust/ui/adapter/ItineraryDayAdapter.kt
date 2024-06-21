package com.dicoding.wanderlust.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.databinding.ItemItineraryDayBinding
import com.dicoding.wanderlust.remote.response.PlanItem
import com.dicoding.wanderlust.ui.destination.DestinationDetailActivity

class ItineraryDayAdapter(private val planItems: List<PlanItem>) : RecyclerView.Adapter<ItineraryDayAdapter.ItineraryDayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryDayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemItineraryDayBinding.inflate(inflater, parent, false)
        return ItineraryDayViewHolder(binding)
    }

    override fun getItemCount(): Int = planItems.size

    override fun onBindViewHolder(holder: ItineraryDayViewHolder, position: Int) {
        val planItem = planItems[position]
        holder.bind(planItem)
    }

    inner class ItineraryDayViewHolder(private val binding: ItemItineraryDayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(planItem: PlanItem) {
            binding.tvDay.text = itemView.context.getString(R.string.day_count, planItem.day ?: 0)

            val destinationAdapter = DestinationAdapter { destination ->
                val intent = Intent(itemView.context, DestinationDetailActivity::class.java)
                intent.putExtra(DestinationDetailActivity.EXTRA_DESTINATION, destination)
                itemView.context.startActivity(intent)
            }

            // Setup RecyclerView for destinations
            binding.rvDestination.adapter = destinationAdapter
            binding.rvDestination.layoutManager = LinearLayoutManager(itemView.context)

            val destinations = planItem.destinations ?: emptyList()
            destinationAdapter.submitList(destinations)

            // Log and handle empty destinations
            Log.d(TAG, "Binding PlanItem: ${planItem.day}, Destinations: ${destinations.size}")
            if (destinations.isEmpty()) {
                Log.w(TAG, "No destinations found for day ${planItem.day}")
            }
        }
    }


    companion object {
        private const val TAG = "ItineraryDayAdapter"
    }
}

