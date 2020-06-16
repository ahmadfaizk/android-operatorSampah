package com.banksampah.operator.model

import com.google.gson.annotations.SerializedName

data class Customer(
    val id: Int,
    val name: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    val address: String?,
    val password: String?,
    @SerializedName("forgot_password")
    val isForgotPassword: Boolean?,
    val date: String?,
    val balance: Long?,
    val withdraw: Long?
)