package com.banksampah.operator.network.response

data class MultiResponse<T>(
    var error: Boolean,
    var message: String,
    var errors_detail: List<String>? = null,
    var data: List<T>
)