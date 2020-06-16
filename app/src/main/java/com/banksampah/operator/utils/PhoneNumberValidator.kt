package com.banksampah.operator.utils

import java.util.regex.Pattern

object PhoneNumberValidator {
    private const val regex = "^[+62]?[0-9]{10,13}"

    fun validate(number: String): Boolean {
        return Pattern.matches(regex, number)
    }

    fun clean(number: String) : String {
        if (number.substring(0, 3) == "+62") {
            return number.replace("+62", "0")
        } else if (number.substring(0, 2) == "62") {
            return number.replace("62", "0")
        }
        return number
    }
}