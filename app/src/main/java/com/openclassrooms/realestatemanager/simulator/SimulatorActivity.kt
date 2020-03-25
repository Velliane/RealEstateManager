package com.openclassrooms.realestatemanager.simulator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Injection

class SimulatorActivity : AppCompatActivity(), View.OnClickListener {

    /** TextInput */
    private lateinit var capitalTxt: TextInputEditText
    private lateinit var durationTxt: TextInputEditText
    private lateinit var rateTxt: TextInputEditText

    /** Calculate Button */
    private lateinit var calculateBtn: MaterialButton

    /** Result TextView */
    private lateinit var result: TextView

    private lateinit var viewModel: SimulatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulator)

        capitalTxt = findViewById(R.id.simulator_edit_capital)
        durationTxt = findViewById(R.id.simulator_edit_duration)
        rateTxt = findViewById(R.id.simulator_edit_rate)
        calculateBtn = findViewById(R.id.reset_research)
        calculateBtn.setOnClickListener(this)
        result = findViewById(R.id.simulator_result)

        val viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SimulatorViewModel::class.java)
    }

    override fun onClick(view: View?){
        when(view){
            calculateBtn -> {
                val rate = rateTxt.text.toString().toFloat()
                viewModel.calculateMonthlyPayment(capitalTxt.text.toString().toInt(), durationTxt.text.toString().toInt(), rate).observe(this, Observer {
                    result.text = getString(R.string.result, it.toString())
                })

            }
        }
    }
}
