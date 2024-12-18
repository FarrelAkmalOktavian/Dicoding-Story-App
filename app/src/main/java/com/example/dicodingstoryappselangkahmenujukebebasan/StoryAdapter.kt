package com.example.dicodingstoryappselangkahmenujukebebasan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.ListStoryItem
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ItemListBinding

class StoryAdapter(private var listStory: List<ListStoryItem>, var onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.apply {
                name.text = story.name
                storyDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(storyImage)

                itemView.setOnClickListener {
                    onItemClick(story.id ?: "")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newListStory: List<ListStoryItem>) {
        listStory = newListStory
        notifyDataSetChanged()
    }
}
