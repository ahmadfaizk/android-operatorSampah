package com.banksampah.operator.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object RupiahFormatter {
    private val price = DecimalFormat.getCurrencyInstance() as DecimalFormat
    private val rupiah = DecimalFormatSymbols()

    init {
        rupiah.currencySymbol = "Rp. "
        rupiah.monetaryDecimalSeparator = ','
        rupiah.groupingSeparator = '.'
        price.decimalFormatSymbols = rupiah
    }

    fun format(money: Double): String {
        return price.format(money)
    }
}