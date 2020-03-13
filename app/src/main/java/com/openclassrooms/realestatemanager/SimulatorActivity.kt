package com.openclassrooms.realestatemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_simulator.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.pow

class SimulatorActivity : AppCompatActivity(), View.OnClickListener {

    /** TextInput */
    private lateinit var capitalTxt: TextInputEditText
    private lateinit var durationTxt: TextInputEditText
    private lateinit var rateTxt: TextInputEditText

    /** Calculate Button */
    private lateinit var calculateBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulator)

        capitalTxt = findViewById(R.id.simulator_edit_capital)
        durationTxt = findViewById(R.id.simulator_edit_duration)
        rateTxt = findViewById(R.id.simulator_edit_rate)
        calculateBtn = findViewById(R.id.calculate_button)
        calculateBtn.setOnClickListener(this)
    }

    private fun calculateMonthlyPayment(capital: Int, duration: Int, rate: Int): BigDecimal{
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
        val monthly = first.divide(second, 2 , RoundingMode.HALF_UP)
        simulator_result.text = monthly.toString()
        return monthly
    }

    override fun onClick(view: View?){
        when(view){
            calculateBtn -> {
                calculateMonthlyPayment(capitalTxt.text.toString().toInt(), durationTxt.text.toString().toInt(), rateTxt.text.toString().toInt())
            }
        }
    }
}
