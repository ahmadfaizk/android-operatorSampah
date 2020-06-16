package com.banksampah.operator.ui.complaint

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.banksampah.operator.R
import com.banksampah.operator.model.Complaint
import kotlinx.android.synthetic.main.item_complaint.view.*

class ComplaintAdapter: RecyclerView.Adapter<ComplaintAdapter.ViewHolder>() {

    private val complaints = ArrayList<Complaint>()

    fun setData(data: List<Complaint>) {
        if (complaints.isNotEmpty()) {
            complaints.clear()
        }
        complaints.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_complaint, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = complaints.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(complaints[position])
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(complaint: Complaint) {
            with(itemView) {
                tv_name.text = complaint.name
                tv_complaint.text = complaint.text
                tv_date.text = complaint.date
            }
        }
    }
}