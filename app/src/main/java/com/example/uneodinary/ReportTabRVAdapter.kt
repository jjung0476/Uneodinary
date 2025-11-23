package com.example.uneodinary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uneodinary.databinding.ItemTagFilterBinding

class ReportTabRVAdapter (private val tabList: ArrayList<TabItem>) : RecyclerView.Adapter<ReportTabRVAdapter.ViewHolder>() {
    interface MyItemClickListener {
        fun onItemClick(tab: TabItem)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun addItem(tab: TabItem) {
        tabList.add(tab)
        notifyItemInserted(tabList.size - 1)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTagFilterBinding = ItemTagFilterBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tabItem = tabList[position]

        holder.bind(tabItem)

        holder.itemView.setOnClickListener {
            mItemClickListener.onItemClick(tabItem)
        }
    }

    override fun getItemCount(): Int = tabList.size

    inner class ViewHolder(val binding: ItemTagFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tab: TabItem) {
            binding.lookChipTitle01Tv.text = tab.tagName
        }
    }
}