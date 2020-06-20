package com.banksampah.operator.ui.profile

import android.app.AlertDialog
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
import com.banksampah.operator.ui.profile.update.ProfileUpdateFragment
import com.banksampah.operator.utils.TokenPreference
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var user: User? = null
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = TokenPreference.getInstance(requireContext()).getToken().toString()
        requestData()
        btn_update.setOnClickListener {
            showDialogUpdate()
        }
    }

    private fun requestData() {
        showLoading(true)
        ApiClient.instances.getUser("Bearer $token").enqueue(object :
            Callback<SingleResponse<User>> {
            override fun onResponse(call: Call<SingleResponse<User>>, response: Response<SingleResponse<User>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    user = response.body()?.data
                    user?.let { populateData(it) }
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

    private fun showDialogUpdate() = AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.profile_update_message))
        .setItems(R.array.items_profile
        ) { _, which ->
            when(which) {
                0 -> {
                    val bundle = Bundle()
                    bundle.putParcelable(ProfileUpdateFragment.EXTRA_USER, user)
                    view?.findNavController()?.navigate(R.id.action_nav_profile_to_profileUpdateFragment, bundle)
                }
                1 -> view?.findNavController()?.navigate(R.id.action_nav_profile_to_changePasswordFragment)
            }
        }
        .create()
        .show()

    private fun populateData(user: User) {
        tv_name?.text = user.name
        tv_phone_number?.text = user.phoneNumber
        tv_address?.text = user.address
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