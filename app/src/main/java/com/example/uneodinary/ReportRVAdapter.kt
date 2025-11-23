package com.example.uneodinary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uneodinary.databinding.ItemReportCardBinding

class ReportRVAdapter(private val reportList: ArrayList<Report>) : RecyclerView.Adapter<ReportRVAdapter.ViewHolder>() {
    interface MyItemClickListener {
        fun onItemClick(report: Report)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: ReportRVAdapter.MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun addItem(report: Report) {
        reportList.add(report)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemReportCardBinding = ItemReportCardBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reportItem = reportList[position]

        holder.bind(reportItem)

        holder.itemView.setOnClickListener {
            mItemClickListener.onItemClick(reportItem)
        }
    }

    override fun getItemCount(): Int = reportList.size

    inner class ViewHolder(val binding: ItemReportCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report) {
            binding.root.setOnClickListener {
                binding.cardAmountTv.text = report.total.toString() + "원"
                binding.reportDate1.text = report.date + " 보고서"

                binding.shareIcon1.setOnClickListener {
                    // 카톡 공유 로직
                }
            }
        }
    }
}