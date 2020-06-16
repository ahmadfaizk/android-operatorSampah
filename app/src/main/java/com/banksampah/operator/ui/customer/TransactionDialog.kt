package com.banksampah.operator.ui.customer

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.banksampah.operator.R
import kotlinx.android.synthetic.main.dialog_transaction.view.*

class TransactionDialog : DialogFragment() {

    private var onSendClicked: OnSendClicked? = null

    fun setOnSendClicked(onSendClicked: OnSendClicked) {
        this.onSendClicked = onSendClicked
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_transaction, null)
            builder.setView(view)
                .setPositiveButton("Kirim") { _, _ ->
                    val amount = view.edt_amount.text.toString().trim()
                    onSendClicked?.onClick(amount)
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw  IllegalStateException("Activity cannot be null")
    }

    interface OnSendClicked {
        fun onClick(amount: String)
    }

    enum class Transaction {
        DEPOSIT, WITHDRAW
    }
}