package com.banksampah.operator.ui.profile.update

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.banksampah.operator.R
import com.banksampah.operator.model.User
import com.banksampah.operator.network.ApiClient
import com.banksampah.operator.network.response.SingleResponse
import com.banksampah.operator.ui.login.LoginFragment
import com.banksampah.operator.utils.TokenPreference
import kotlinx.android.synthetic.main.fragment_profile_update.*
import kotlinx.android.synthetic.main.fragment_profile_update.edt_phone_number
import kotlinx.android.synthetic.main.fragment_profile_update.progress_bar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileUpdateFragment : Fragment() {

    companion object {
        const val EXTRA_USER = "extra_user"
    }

    private var user: User? = null
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = TokenPreference.getInstance(requireContext()).getToken().toString()
        user = arguments?.getParcelable(EXTRA_USER)
        populateData()
        setContactListener()
        btn_update.setOnClickListener {
            validateForm()
        }
    }

    private fun populateData() {
        edt_full_name.setText(user?.name)
        edt_phone_number.setText(user?.phoneNumber)
        edt_address.setText(user?.address)
    }

    private fun validateForm() {
        val name = edt_full_name.text.toString().trim()
        val phoneNumber = edt_phone_number.text.toString().trim()
        val address = edt_address.text.toString().trim()

        if (name.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
            showMessage(getString(R.string.form_empty))
            return
        }
        update(name, phoneNumber, address)
    }

    private fun update(name: String, phoneNumber: String, address: String) {
        showLoading(true)
        ApiClient.instances.updateUser("Bearer $token", name, phoneNumber, address).enqueue(object :
            Callback<SingleResponse<User>> {
            override fun onResponse(call: Call<SingleResponse<User>>, response: Response<SingleResponse<User>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    view?.findNavController()?.navigate(R.id.action_profileUpdateFragment_to_nav_profile)
                    showMessage(getString(R.string.update_profile_success))
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

    private fun setContactListener() {
        edt_phone_number.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edt_phone_number.right - edt_phone_number.compoundDrawables[2].bounds.width())) {
                        readContact()
                        return true
                    }
                }
                return false
            }
        })
    }

    @SuppressLint("IntentReset")
    private fun readContact() {
        val contact = Intent(Intent.ACTION_PICK)
        contact.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(contact, LoginFragment.REQUEST_CONTACT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoginFragment.REQUEST_CONTACT && resultCode == Activity.RESULT_OK) {
            getContactFromUri(data?.data)
        }
    }

    private fun cleanNumber(number: String?) : String? {
        return number?.replace("-", "")?.replace(" ", "")
    }

    private fun getContactFromUri(uri: Uri?) {
        if (uri != null) {
            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
            cursor?.moveToFirst()
            val phoneNumber = cursor?.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            edt_phone_number.setText(cleanNumber(phoneNumber))
        }
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