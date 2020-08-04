package com.banksampah.operator.ui.card

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.banksampah.operator.R
import com.banksampah.operator.model.Customer
import com.banksampah.operator.network.ApiClient
import com.banksampah.operator.network.response.MultiResponse
import com.banksampah.operator.network.response.SingleResponse
import com.banksampah.operator.utils.TokenPreference
import kotlinx.android.synthetic.main.fragment_register_card.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterCardFragment : Fragment() {

    private lateinit var customerAdapter: ArrayAdapter<Customer>
    private lateinit var customers: List<Customer>
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = TokenPreference.getInstance(requireContext()).getToken().toString()
        customerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        sp_customer.adapter = customerAdapter
        requestDataCustomer()
        btn_send.setOnClickListener {
            validateInput()
        }
    }

    private fun requestDataCustomer() {
        showLoading(true)
        ApiClient.instances.getCustomers("Bearer $token").enqueue(object :
            Callback<MultiResponse<Customer>> {
            override fun onResponse(call: Call<MultiResponse<Customer>>, response: Response<MultiResponse<Customer>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    response.body()?.data?.let {
                        customers = it
                        customerAdapter.addAll(it)
                    }
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

    private fun validateInput() {
        val idCard = edt_id_card.text.toString().trim()
        val idCustomer = customers[sp_customer.selectedItemPosition].id
        if (idCard.isEmpty()) {
            showMessage("ID Card masih kosong")
            return
        }
        if (idCard.length > 8) {
            showMessage("ID Card melebihi 8 karakter")
            return
        }
        registerCard(idCustomer, idCard)
    }

    private fun registerCard(idCustomer: Int, idCard: String) {
        showLoading(true)
        ApiClient.instances.registerCard("Bearer $token", idCustomer, idCard).enqueue(object : Callback<SingleResponse<Customer>> {
            override fun onResponse(call: Call<SingleResponse<Customer>>, response: Response<SingleResponse<Customer>>) {
                showLoading(false)
                response.body()?.let {
                    if (!it.error) {
                        showMessage("Kartu Berhasil Didaftarkan")
                        edt_id_card.text = null
                        sp_customer.setSelection(0)
                    } else {
                        showMessage(it.message)
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse<Customer>>, t: Throwable) {
                showLoading(false)
                showMessage(t.message.toString())
            }
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar?.visibility = View.VISIBLE
            container?.visibility = View.GONE
        } else {
            progress_bar?.visibility = View.GONE
            container?.visibility = View.VISIBLE
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}