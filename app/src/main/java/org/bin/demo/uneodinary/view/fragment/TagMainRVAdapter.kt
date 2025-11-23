package org.bin.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.uneodinary.databinding.ItemTagViewBinding

class TagMainRVAdapter() : RecyclerView.Adapter<TagMainRVAdapter.ViewHolder>() {

    private var tagList: List<TagSummary> = emptyList()

    interface MyItemClickListener {
        fun onRemoveTag(tag: TagSummary)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun updateTagList(newList: List<TagSummary>) {
        tagList = newList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTagViewBinding = ItemTagViewBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tagItem = tagList[position]

        holder.bind(tagItem)

        holder.binding.tagClose.setOnClickListener {
            mItemClickListener.onRemoveTag(tagItem)
        }
    }

    override fun getItemCount(): Int = tagList.size

    inner class ViewHolder(val binding: ItemTagViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: TagSummary) {
            // tagClose 버튼은 binding에 포함되어 있다고 가정합니다.
            binding.tagText.text = tag.tagName
        }
    }
}