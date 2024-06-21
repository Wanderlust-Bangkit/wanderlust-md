package com.dicoding.wanderlust.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.databinding.ItemItineraryBinding
import com.dicoding.wanderlust.remote.response.ItineraryItem

class ItineraryAdapter(private val onItemClick: (ItineraryItem) -> Unit) :
    ListAdapter<ItineraryItem, ItineraryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemItineraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itinerary = getItem(position)
        holder.bind(itinerary)
        holder.itemView.setOnClickListener {
            onItemClick(itinerary)
        }
    }

    class MyViewHolder(private val binding: ItemItineraryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itinerary: ItineraryItem) {
            binding.apply {
                tvItineraryTitle.text = itinerary.nameItinerary
                val startDateStr = itinerary.startDate
                val endDateStr = itinerary.endDate
                tvItineraryDate.text = itemView.context.getString(R.string.date_itinerary, startDateStr, endDateStr)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItineraryItem>() {
            override fun areItemsTheSame(oldItem: ItineraryItem, newItem: ItineraryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ItineraryItem, newItem: ItineraryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
