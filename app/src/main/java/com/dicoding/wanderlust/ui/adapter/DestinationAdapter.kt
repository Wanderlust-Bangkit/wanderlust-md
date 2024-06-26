package com.dicoding.wanderlust.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.wanderlust.databinding.ItemDestinationBinding
import com.dicoding.wanderlust.remote.response.DataItem
import kotlin.math.min

class DestinationAdapter(private val onItemClick: (DataItem) -> Unit) :
    ListAdapter<DataItem, DestinationAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDestinationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val destination = getItem(position)
        holder.bind(destination)
        holder.itemView.setOnClickListener {
            onItemClick(destination)
        }
    }

    override fun getItemCount(): Int {
        // Ensure only top 8 nearest destinations are displayed
        return min(super.getItemCount(), 8)
    }

    inner class MyViewHolder(private val binding: ItemDestinationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(destination: DataItem) {
            binding.apply {
                tvNamaDestinasi.text = destination.placeName
                tvDeskripsiDestinasi.apply {
                    text = destination.description
                    maxLines = 3
                    ellipsize = TextUtils.TruncateAt.END
                }
                tvLokasiDestinasi.text = destination.city

                itemView.setOnClickListener {
                    onItemClick(destination)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.placeId == newItem.placeId
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
