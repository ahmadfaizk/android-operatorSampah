package com.banksampah.operator.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    val address: String?
): Parcelable