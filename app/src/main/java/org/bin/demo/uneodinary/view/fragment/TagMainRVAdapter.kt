package org.bin.demo.uneodinary.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.bin.demo.uneodinary.databinding.ItemTagViewBinding

class TagMainRVAdapter(private val tagList: ArrayList<String>) : RecyclerView.Adapter<TagMainRVAdapter.ViewHolder>() {
    interface MyItemClickListener {
        fun onRemoveTag(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun addItem(tag: String) {
        tagList.add(tag)
        notifyItemInserted(tagList.size - 1)
    }

    fun removeItem(position: Int) {
        tagList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTagViewBinding = ItemTagViewBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tagItem = tagList[position]

        // 데이터 바인딩
        holder.bind(tagItem)

        // 아이템 전체 클릭 리스너 (선택적)
        holder.itemView.setOnClickListener {
//            mItemClickListener.onItemClick(tagItem)
        }

        holder.binding.tagClose.setOnClickListener {
            mItemClickListener.onRemoveTag(position)
        }
    }

    override fun getItemCount(): Int = tagList.size

    inner class ViewHolder(val binding: ItemTagViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: String) {
            binding.tagText.text = tag
        }
    }
}