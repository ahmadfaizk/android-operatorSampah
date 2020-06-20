package com.banksampah.operator.ui.complaint

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.banksampah.operator.R
import com.banksampah.operator.model.Complaint
import com.banksampah.operator.network.ApiClient
import com.banksampah.operator.network.response.MultiResponse
import com.banksampah.operator.utils.TokenPreference
import kotlinx.android.synthetic.main.fragment_complaint.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplaintFragment : Fragment() {

    private lateinit var adapter: ComplaintAdapter
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complaint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = TokenPreference.getInstance(requireContext()).getToken().toString()
        adapter = ComplaintAdapter()
        rv_complaint.layoutManager = LinearLayoutManager(context)
        rv_complaint.adapter = adapter
        requestData()
    }

    private fun requestData() {
        showLoading(true)
        ApiClient.instances.getComplaints("Bearer $token").enqueue(object : Callback<MultiResponse<Complaint>> {
            override fun onResponse(call: Call<MultiResponse<Complaint>>, response: Response<MultiResponse<Complaint>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    response.body()?.data?.let {populateData(it) }
                } else {
                    showMessage(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<MultiResponse<Complaint>>, t: Throwable) {
                showLoading(false)
                showMessage(t.message.toString())
            }
        })
    }

    private fun populateData(data: List<Complaint>) {
        adapter.setData(data)
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar?.visibility = View.VISIBLE
            rv_complaint?.visibility = View.GONE
        } else {
            progress_bar?.visibility = View.GONE
            rv_complaint?.visibility = View.VISIBLE
        }
    }
}