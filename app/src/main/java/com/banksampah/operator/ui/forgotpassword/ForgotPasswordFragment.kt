package com.banksampah.operator.ui.forgotpassword

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
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_send.setOnClickListener {
            validateForm()
        }
        setContactListener()
    }

    private fun validateForm() {
        var phoneNumber = edt_phone_number.text.toString().trim()
        val password = edt_password.text.toString().trim()
        val password2 = edt_password_2.text.toString().trim()
        if (phoneNumber.isEmpty()) {
            showMessage("Anda Belum Mengisi Nomor Handphone atau Password")
            return
        }
        if (password != password2) {
            showMessage("Password Baru Anda Tidak Cocok.\nSilahkan cek kembali.")
            return
        }
        if (!PhoneNumberValidator.validate(phoneNumber)) {
            showMessage("Format Nomor Handphone Salah")
            return
        }

        phoneNumber = PhoneNumberValidator.clean(phoneNumber)
        sendRequest(phoneNumber, password)
    }

    private fun sendRequest(phoneNumber: String, password: String) {
        showLoading(true)
        ApiClient.instances.forgotPassword(phoneNumber, password).enqueue(object :
            Callback<SingleResponse<Token>> {
            override fun onResponse(call: Call<SingleResponse<Token>>, response: Response<SingleResponse<Token>>) {
                showLoading(false)
                val error = response.body()?.error
                if (error != null && !error) {
                    showMessage("Request Berhasil\nSilahkan Tunggu SMS Balasan dari Operator Kami.")
                    view?.findNavController()?.navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                } else {
                    showMessage(response.body()?.message.toString())
                }
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
            val phoneNumber = cursor?.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            edt_phone_number.setText(cleanNumber(phoneNumber))
        }
    }

    private fun cleanNumber(number: String?) : String? {
        return number?.replace("-", "")?.replace(" ", "")
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