package com.banksampah.operator.ui.customer.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.banksampah.operator.R
import com.banksampah.operator.model.History
import com.banksampah.operator.utils.RupiahFormatter
import kotlinx.android.synthetic.main.item_customer_history.view.*

class CustomerHistoryAdapter : RecyclerView.Adapter<CustomerHistoryAdapter.ViewHolder>() {

    private val histories = ArrayList<History>()
    private var onClickListener : OnClickListener? = null

    fun setData(histories: List<History>) {
        if (this.histories.isNotEmpty()) {
            this.histories.clear()
        }
        this.histories.addAll(histories)
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = histories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = histories[position]
        holder.bind(history)
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(history)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(history: History) {
            with(itemView) {
                if (history.type == "withdraw") {
                    tv_title.text = context.getString(R.string.melakukan_penarikan_saldo)
                } else {
                    tv_title.text = context.getString(R.string.pickup_sampah_saldo_ditambahkan)
                }
                tv_amount.text = RupiahFormatter.format(history.amount.toDouble())
                tv_date.text = history.date
            }
        }
    }

    interface OnClickListener {
        fun onClick(history: History)
    }
}