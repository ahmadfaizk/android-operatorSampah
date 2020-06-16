package com.banksampah.operator.utils

import android.annotation.SuppressLint
import android.content.Context
import com.banksampah.operator.R

class TokenPreference (context: Context) {
    companion object {
        private var instance: TokenPreference? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: TokenPreference(context)
            }
    }

    private val sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferense_file_key), Context.MODE_PRIVATE)

    fun saveToken(token: String?) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken() : String? = sharedPreferences.getString("token", null)

    @SuppressLint("CommitPrefEdits")
    fun removeToken() {
        sharedPreferences.edit().remove("token").apply()
    }
}