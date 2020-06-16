package com.banksampah.operator.model

import com.google.gson.annotations.SerializedName

data class Complaint(
    val id: Int,
    val text: String,
    val name: String,
    val address: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    val date: String
)