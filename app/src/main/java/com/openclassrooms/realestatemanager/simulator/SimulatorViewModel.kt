package com.openclassrooms.realestatemanager.simulator

import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class SimulatorViewModel: ViewModel() {

    fun calculateMonthlyPayment(capital: Int, duration: Int, rate: Int): BigDecimal {
        //-- calculate capital*(rate/12) --//
        val rateInInt = BigDecimal(rate).divide(BigDecimal(100))
        val durationInMonth = BigDecimal(duration).multiply(BigDecimal(12)).negate().toInt()
        val first = BigDecimal(capital).multiply(rateInInt).divide(BigDecimal(12), 2, RoundingMode.HALF_UP)
        //-- calculate 1-(1+(rate/12))^durationInMonth --//
        val ratePerMonth = rateInInt.divide(BigDecimal(12), 5, RoundingMode.HALF_UP)
        val onePlusRatePermMonth = BigDecimal(1).add(ratePerMonth)
        val exponent = onePlusRatePermMonth.pow(durationInMonth, MathContext.DECIMAL128)
        val second = BigDecimal(1).subtract(exponent)
        //-- divide the two result --//
        return first.divide(second, 2 , RoundingMode.HALF_UP)
    }
}