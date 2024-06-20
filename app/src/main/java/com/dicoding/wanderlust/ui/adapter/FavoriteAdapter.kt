package com.dicoding.wanderlust.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.wanderlust.databinding.ItemFavoriteBinding
import com.dicoding.wanderlust.remote.response.DataItem

class FavoriteAdapter(private val onItemClick: (DataItem) -> Unit) :
    ListAdapter<DataItem, FavoriteAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val destination = getItem(position)
        holder.bind(destination)
        holder.itemView.setOnClickListener {
            onItemClick(destination)
        }
    }

    class MyViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(destination: DataItem) {
            binding.apply {
                tvNamaDestinasi.text = destination.placeName
                tvLokasiDestinasi.text = destination.city

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

