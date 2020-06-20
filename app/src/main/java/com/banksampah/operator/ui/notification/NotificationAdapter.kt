package com.banksampah.operator.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.banksampah.operator.R
import com.banksampah.operator.model.Customer
import kotlinx.android.synthetic.main.item_customer_register.view.*

class NotificationAdapter: RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_register, parent, false)
        return ViewHolder(
            view
        )
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
                if (customer.isForgotPassword != null && customer.isForgotPassword) {
                    tv_name.text = context.resources.getString(R.string.customer_forgot_password_template, customer.name)
                } else {
                    tv_name.text = context.resources.getString(R.string.customer_register_template, customer.name)
                }
                tv_date.text = context.resources.getString(R.string.time_template, customer.date)
            }
        }
    }

    interface OnClickListener {
        fun onClick(customer: Customer)
    }
}