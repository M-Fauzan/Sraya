package com.dicoding.fauzan.sraya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.fauzan.sraya.Constants.SEND_ID
import com.dicoding.fauzan.sraya.databinding.ItemMessageBinding

class MessagingAdapter : ListAdapter<Message, MessagingAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            if (message.id == SEND_ID) {
                binding.tvMessageHuman.apply {
                    visibility = View.VISIBLE
                    text = message.message
                }
                binding.tvMessageBot.visibility = View.GONE
            } else {
                binding.tvMessageBot.apply {
                    visibility = View.VISIBLE
                    text = message.message
                }
                binding.tvMessageHuman.visibility = View.GONE
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagingAdapter.ViewHolder {
        return ViewHolder(ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MessagingAdapter.ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.time == newItem.time
            }
        }
    }


}