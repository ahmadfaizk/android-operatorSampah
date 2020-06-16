package com.banksampah.operator.ui.customer

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.banksampah.operator.R
import com.banksampah.operator.model.Customer
import com.banksampah.operator.model.History
import com.banksampah.operator.network.ApiClient
import com.banksampah.operator.network.response.MultiResponse
import com.banksampah.operator.network.response.SingleResponse
import com.banksampah.operator.ui.customer.history.CustomerHistoryFragment
import com.banksampah.operator.utils.TokenPreference
import kotlinx.android.synthetic.main.fragment_customer.progress_bar
import kotlinx.android.synthetic.main.fragment_customer.rv_customers
import kotlinx.android.synthetic.main.fragment_customer.tv_title
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerFragment : Fragment() {

    private lateinit var token: String
    private lateinit var adapter: CustomerAdapter
    private lateinit var transactionDialog: TransactionDialog
    private lateinit var transaction: TransactionDialog.Transaction

    private var idSelected = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = TokenPreference.getInstance(requireContext()).getToken().toString()
        adapter = CustomerAdapter()
        rv_customers.layoutManager = LinearLayoutManager(context)
        rv_customers.adapter = adapter
        requestData()
        transactionDialog = TransactionDialog()
        adapter.setOnClickListener(object : CustomerAdapter.OnClickListener {
            override fun onClick(customer: Customer) {
                idSelected = customer.id
                showTransactionDialog()
            }
        })
        transactionDialog.setOnSendClicked(object : TransactionDialog.OnSendClicked {
            override fun onClick(amount: String) {
                if (amount.isNotEmpty()) {
                    when(transaction) {
                        TransactionDialog.Transaction.DEPOSIT -> deposit(amount.toLong())
                        TransactionDialog.Transaction.WITHDRAW -> withdraw(amount.toLong())
                    }
                } else {
                    showMessage("Jumlah Transaksi Kosong")
                }
            }
        })
    }

    private fun requestData() {
        showLoading(true)
        ApiClient.instances.getCustomers("Bearer $token").enqueue(object :
            Callback<MultiResponse<Customer>> {
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

    private fun deposit(amount: Long) {
        showLoading(true)
        ApiClient.instances.deposit("Bearer $token", idSelected, amount).enqueue(object : Callback<SingleResponse<History>> {
            override fun onResponse(call: Call<SingleResponse<History>>, response: Response<SingleResponse<History>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    showMessage("Berhasil Melakukan Deposit")
                    requestData()
                } else {
                    showMessage(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<SingleResponse<History>>, t: Throwable) {
                showLoading(false)
                showMessage(t.message.toString())
            }
        })
    }

    private fun withdraw(amount: Long) {
        showLoading(true)
        ApiClient.instances.withdraw("Bearer $token", idSelected, amount).enqueue(object : Callback<SingleResponse<History>> {
            override fun onResponse(call: Call<SingleResponse<History>>, response: Response<SingleResponse<History>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    showMessage("Berhasil Melakukan Penarikan Saldo")
                    requestData()
                } else {
                    showMessage(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<SingleResponse<History>>, t: Throwable) {
                showLoading(false)
                showMessage(t.message.toString())
            }
        })
    }

    private fun showTransactionDialog() {
        AlertDialog.Builder(context)
            .setTitle("Pilih Jenis Transaksi")
            .setItems(R.array.transactions_type
            ) { dialog, which ->
                when(which) {
                    0 -> {
                        showAmountDialog(TransactionDialog.Transaction.DEPOSIT)
                        dialog.dismiss()
                    }
                    1 -> {
                        showAmountDialog(TransactionDialog.Transaction.WITHDRAW)
                        dialog.dismiss()
                    }
                    2 -> {
                        val bundle = Bundle()
                        bundle.putInt(CustomerHistoryFragment.EXTRA_ID, idSelected)
                        view?.findNavController()?.navigate(R.id.action_nav_customer_to_customerHistoryFragment, bundle)
                    }
                }
            }
            .create()
            .show()
    }

    private fun showAmountDialog(transaction: TransactionDialog.Transaction) {
        this.transaction = transaction
        transactionDialog.show(childFragmentManager, "transaksi")
    }

    private fun populateData(data: List<Customer>) {
        adapter.setData(data)
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