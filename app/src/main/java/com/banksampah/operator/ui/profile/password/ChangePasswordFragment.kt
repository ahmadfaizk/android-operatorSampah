package com.banksampah.operator.ui.profile.password

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.banksampah.operator.R
import com.banksampah.operator.model.User
import com.banksampah.operator.network.ApiClient
import com.banksampah.operator.network.response.SingleResponse
import com.banksampah.operator.utils.TokenPreference
import kotlinx.android.synthetic.main.fragment_change_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment() {

    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = TokenPreference.getInstance(requireContext()).getToken().toString()
        btn_update.setOnClickListener {
            validatePassword()
        }
    }

    private fun validatePassword() {
        val password = edt_password.text.toString().trim()
        val password2 = edt_password_2.text.toString().trim()

        if (password.isEmpty() || password2.isEmpty()) {
            showMessage(getString(R.string.form_empty))
            return
        }
        if (password != password2) {
            showMessage(getString(R.string.new_password_not_same))
            return
        }
        sendRequest(password)
    }

    private fun sendRequest(password: String) {
        showLoading(true)
        ApiClient.instances.changePassword("Bearer $token", password).enqueue(object :
            Callback<SingleResponse<User>> {
            override fun onResponse(call: Call<SingleResponse<User>>, response: Response<SingleResponse<User>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    view?.findNavController()?.navigate(R.id.action_changePasswordFragment_to_nav_profile)
                    showMessage(getString(R.string.change_password_success))
                } else {
                    showMessage(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<SingleResponse<User>>, t: Throwable) {
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