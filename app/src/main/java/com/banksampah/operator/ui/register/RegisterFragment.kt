package com.banksampah.operator.ui.register

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
import com.banksampah.operator.model.Token
import com.banksampah.operator.network.ApiClient
import com.banksampah.operator.network.response.SingleResponse
import com.banksampah.operator.ui.login.LoginFragment
import com.banksampah.operator.utils.PhoneNumberValidator
import com.banksampah.operator.utils.TokenPreference
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.btn_register
import kotlinx.android.synthetic.main.fragment_register.edt_password
import kotlinx.android.synthetic.main.fragment_register.edt_phone_number
import kotlinx.android.synthetic.main.fragment_register.progress_bar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContactListener()
        btn_register.setOnClickListener {
            validateForm()
        }
    }

    private fun validateForm() {
        val name = edt_full_name.text.toString().trim()
        var phoneNumber = edt_phone_number.text.toString().trim()
        val password = edt_password.text.toString().trim()

        if (name.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            showMessage("Pastikan Anda Mengisi Semua Datanya")
        }

        if (!PhoneNumberValidator.validate(phoneNumber)) {
            showMessage("Format Nomor Handphone Slah")
            return
        }

        phoneNumber = PhoneNumberValidator.clean(phoneNumber)

        register(name, phoneNumber, password)
    }

    private fun register(name: String, phoneNumber: String, password: String) {
        showLoading(true)
        ApiClient.instances.register(name, phoneNumber, password).enqueue(object :
            Callback<SingleResponse<Token>> {
            override fun onResponse(call: Call<SingleResponse<Token>>, response: Response<SingleResponse<Token>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    showMessage("Anda Berhasil Mendaftar Sebagai Operator")
                    val token = response.body()?.data
                    TokenPreference.getInstance(requireContext()).saveToken(token?.token)
                    view?.findNavController()?.navigate(R.id.action_registerFragment_to_mainActivity)
                    this@RegisterFragment.activity?.finish()
                }
                showMessage(response.body()?.message.toString())
            }

            override fun onFailure(call: Call<SingleResponse<Token>>, t: Throwable) {
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

    private fun getContactFromUri(uri: Uri?) {
        if (uri != null) {
            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
            cursor?.moveToFirst()
            val name = cursor?.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = cursor?.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            edt_phone_number.setText(phoneNumber)
            if (edt_full_name.text.isEmpty()) {
                edt_full_name.setText(name)
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state)
            progress_bar.visibility = View.VISIBLE
        else
            progress_bar.visibility = View.GONE
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}