package com.banksampah.operator.ui.customer.register

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.banksampah.operator.R
import com.banksampah.operator.model.Customer
import kotlinx.android.synthetic.main.item_customer_register.view.*

class CustomerRegisterAdapter: RecyclerView.Adapter<CustomerRegisterAdapter.ViewHolder>() {

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
                tv_phone_number.text = "No. HP : ${customer.phoneNumber}"
                if (customer.isForgotPassword != null && customer.isForgotPassword) {
                    tv_name.text = "${customer.name} Lupa Password"
                } else {
                    tv_name.text = "${customer.name} Mendaftar Sebagai Nasabah Baru"
                }
                tv_date.text = "Pada : ${customer.date}"
            }
        }
    }

    interface OnClickListener {
        fun onClick(customer: Customer)
    }
}