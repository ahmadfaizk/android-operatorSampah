package com.banksampah.operator.ui.customer.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.banksampah.operator.R
import com.banksampah.operator.model.Customer
import com.banksampah.operator.network.ApiClient
import com.banksampah.operator.network.response.MultiResponse
import com.banksampah.operator.utils.TokenPreference
import kotlinx.android.synthetic.main.fragment_customer_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerRegisterFragment : Fragment() {

    private lateinit var token: String
    private lateinit var adapter: CustomerRegisterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = TokenPreference.getInstance(requireContext()).getToken().toString()
        adapter = CustomerRegisterAdapter()
        rv_customers.layoutManager = LinearLayoutManager(context)
        rv_customers.adapter = adapter
        requestData()
        adapter.setOnClickListener(object : CustomerRegisterAdapter.OnClickListener {
            override fun onClick(customer: Customer) {
                sendSms(customer)
            }
        })
    }

    private fun requestData() {
        showLoading(true)
        ApiClient.instances.getCustomersNotConfirmed("Bearer $token").enqueue(object : Callback<MultiResponse<Customer>> {
            override fun onResponse(call: Call<MultiResponse<Customer>>, response: Response<MultiResponse<Customer>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    response.body()?.data?.let {populateData(it) }
                } else {
                    showMessage(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<MultiResponse<Customer>>, t: Throwable) {
                showLoading(false)
                showMessage(t.message.toString())
            }
        })
    }

    private fun sendSms(customer: Customer) {
        val message = if (customer.isForgotPassword != null && customer.isForgotPassword) {
            "Password anda : ${customer.password}\nTerimakasih."
        } else {
            "Selamat Datang ${customer.name} di Bank Sampah\nPassword anda : ${customer.password}\nTerimakasih Telah Melakukan Pendaftaran."
        }
        val sms = Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + customer.phoneNumber))
        sms.putExtra("sms_body", message)
        startActivity(sms)
    }

    private fun populateData(data: List<Customer>) {
        adapter.setData(data)
        tv_title.text = "${data.size} Pemberitahuan"
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar.visibility = View.VISIBLE
            rv_customers.visibility = View.GONE
        } else {
            progress_bar.visibility = View.GONE
            rv_customers.visibility = View.VISIBLE
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}