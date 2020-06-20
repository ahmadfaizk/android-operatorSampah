package com.banksampah.operator.ui.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.banksampah.operator.R
import com.banksampah.operator.model.Customer
import com.banksampah.operator.utils.RupiahFormatter
import kotlinx.android.synthetic.main.item_customer.view.*

class CustomerAdapter: RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    private val customers = ArrayList<Customer>()
    private var onClickListener: OnClickListener? = null

    fun setData(data: List<Customer>) {
        if (customers.isNotEmpty()) {
            customers.clear()
        }
        customers.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = customers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customers[position])
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(customers[position])
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(customer: Customer) {
            with(itemView) {
                tv_name.text = customer.name
                tv_phone_number.text = context.resources.getString(R.string.phone_number_template, customer.phoneNumber)
                val saldo = RupiahFormatter.format(customer.balance.toString().toDouble())
                tv_saldo.text = context.resources.getString(R.string.saldo_template, saldo)
            }
        }
    }

    interface OnClickListener {
        fun onClick(customer: Customer)
    }
}