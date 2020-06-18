package com.banksampah.operator.ui.customer.history

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.banksampah.operator.R
import com.banksampah.operator.model.History
import com.banksampah.operator.network.ApiClient
import com.banksampah.operator.network.response.MultiResponse
import com.banksampah.operator.network.response.SingleResponse
import com.banksampah.operator.ui.customer.TransactionDialog
import com.banksampah.operator.utils.TokenPreference
import kotlinx.android.synthetic.main.fragment_customer_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerHistoryFragment : Fragment() {

    private lateinit var adapter: CustomerHistoryAdapter
    private lateinit var token: String
    private lateinit var transactionDialog: TransactionDialog
    private var idTransactionSelected = 0
    private var idCustomer = 0

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionDialog = TransactionDialog()
        token = TokenPreference.getInstance(requireContext()).getToken().toString()
        adapter = CustomerHistoryAdapter()
        adapter.setOnClickListener(object : CustomerHistoryAdapter.OnClickListener {
            override fun onClick(history: History) {
                idTransactionSelected = history.id
                showTransactionDialog()
            }
        })
        transactionDialog.setOnSendClicked(object : TransactionDialog.OnSendClicked {
            override fun onClick(amount: String) {
                if (amount.isEmpty()) {
                    showMessage("Jumlah Transaksi Kosong")
                } else {
                    edit(amount.toLong())
                }
            }
        })
        rv_history.layoutManager = LinearLayoutManager(context)
        rv_history.adapter = adapter
        val id = arguments?.getInt(EXTRA_ID)
        id?.let {
            idCustomer = it
            requestData(it)
        }
    }

    private fun requestData(id: Int) {
        showLoading(true)
        ApiClient.instances.getCustomerHistory("Bearer $token", id).enqueue(object :
            Callback<MultiResponse<History>> {
            override fun onResponse(call: Call<MultiResponse<History>>, response: Response<MultiResponse<History>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    val data = response.body()?.data
                    data?.let { adapter.setData(it) }
                } else {
                    showMessage(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<MultiResponse<History>>, t: Throwable) {
                showLoading(false)
                showMessage(t.message.toString())
            }
        })
    }

    private fun showTransactionDialog() {
        AlertDialog.Builder(context)
            .setTitle("Pilih Transaksi Anda")
            .setItems(R.array.items_history
            ) { _, which ->
                when(which) {
                    0 -> showEditDialog()
                    1 -> showDeleteDialog()
                    else -> showMessage("Tidak ada")
                }
            }
            .create()
            .show()
    }

    private fun showEditDialog() {
        transactionDialog.show(childFragmentManager, "Transaksi")
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(context)
            .setMessage("Apakah Anda Yakin Akan Menghapus Transaksi Ini?")
            .setPositiveButton("Ya"
            ) { _, _ -> delete() }
            .setNegativeButton("Tidak"
            ) { dialog, _ -> dialog?.dismiss() }
            .create()
            .show()
    }

    private fun edit(amount: Long) {
        showLoading(true)
        ApiClient.instances.editTransaction("Bearer $token", idTransactionSelected, amount).enqueue(object : Callback<SingleResponse<History>> {
            override fun onResponse(call: Call<SingleResponse<History>>, response: Response<SingleResponse<History>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    showMessage("Berhasil Mengubah Transaksi")
                    requestData(idCustomer)
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

    private fun delete() {
        showLoading(true)
        ApiClient.instances.deleteTransaction("Bearer $token", idTransactionSelected).enqueue(object : Callback<SingleResponse<History>> {
            override fun onResponse(call: Call<SingleResponse<History>>, response: Response<SingleResponse<History>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    showMessage("Berhasil Menghapus")
                    requestData(idCustomer)
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

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar.visibility = View.VISIBLE
            rv_history.visibility = View.GONE
        } else {
            progress_bar.visibility = View.GONE
            rv_history.visibility = View.VISIBLE
        }
    }
}