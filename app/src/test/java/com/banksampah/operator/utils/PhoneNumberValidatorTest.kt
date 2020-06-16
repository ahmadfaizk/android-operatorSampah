package com.banksampah.operator.utils

import org.junit.Test

import org.junit.Assert.*

class PhoneNumberValidatorTest {

    @Test
    fun validate() {
        val number = "+6281232768728"
        val result = PhoneNumberValidator.validate(number)
        val clean = PhoneNumberValidator.clean(number)
        println(result)
        println(clean)
        assertTrue(result)
    }
}