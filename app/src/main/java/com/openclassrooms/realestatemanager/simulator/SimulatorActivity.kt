package com.openclassrooms.realestatemanager.simulator

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection
import com.openclassrooms.realestatemanager.utils.Utils
import java.text.NumberFormat
import java.util.*

class SimulatorActivity : AppCompatActivity(), View.OnClickListener {

    /** TextInput */
    private lateinit var capitalTxt: TextInputEditText
    private lateinit var durationTxt: TextInputEditText
    private lateinit var rateTxt: TextInputEditText
    /** TextInputLayout */
    private lateinit var capitalContainer: TextInputLayout
    private lateinit var durationContainer: TextInputLayout
    private lateinit var rateContainer: TextInputLayout
    /** Calculate Button */
    private lateinit var calculateBtn: MaterialButton
    /** Result TextView */
    private lateinit var result: TextView
    /** ViewModel */
    private lateinit var viewModel: SimulatorViewModel
    /** Toolbar */
    private lateinit var toolbar: Toolbar
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulator)

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        capitalTxt = findViewById(R.id.simulator_edit_capital)
        durationTxt = findViewById(R.id.simulator_edit_duration)
        rateTxt = findViewById(R.id.simulator_edit_rate)
        calculateBtn = findViewById(R.id.reset_research)
        calculateBtn.setOnClickListener(this)
        result = findViewById(R.id.simulator_result)
        capitalContainer = findViewById(R.id.simulator_container_capital)
        durationContainer = findViewById(R.id.simulator_container_duration)
        rateContainer = findViewById(R.id.simulator_container_rate)

        val viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SimulatorViewModel::class.java)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.toolbar_menu_modify)?.isVisible = false
        menu?.findItem(R.id.toolbar_menu_search)?.isVisible = false
        menu?.findItem(R.id.toolbar_menu_add)?.isVisible = false
        return true
    }

    override fun onClick(view: View?){
        when(view){
            calculateBtn -> {
                if(checkRequiredInfo()) {
                    val rate = rateTxt.text.toString().toFloat()
                    val currency = sharedPreferences.getInt(Constants.PREF_CURRENCY, 0)

                    viewModel.calculateMonthlyPayment(capitalTxt.text.toString().toInt(), durationTxt.text.toString().toInt(), rate).observe(this, Observer {
                        val format = NumberFormat.getCurrencyInstance()
                        format.maximumFractionDigits = 0
                        val monthly: String
                        if(currency == 0){
                            format.currency = Currency.getInstance("USD")
                            monthly= format.format(it.toInt())
                        }else{
                            format.currency = Currency.getInstance("EUR")
                            monthly = format.format(it.toInt())
                        }

                        result.text = getString(R.string.result, monthly)
                    })
                    capitalContainer.error = null
                    durationContainer.error = null
                    rateContainer.error = null
                }else{
                    result.text = null
                    if(capitalTxt.text.toString() == ""){
                        capitalContainer.error = getString(R.string.error_simulator_capital)
                    }else{
                        capitalContainer.error = null
                    }
                    if (durationTxt.text.toString() == ""){
                        durationContainer.error = getString(R.string.error_simulator_length)
                    }else{
                        durationContainer.error = null
                    }
                    if(rateTxt.text.toString() == ""){
                        rateContainer.error = getString(R.string.error_simulator_rate)
                    }else{
                        rateContainer.error = null
                    }
                }
            }
        }
    }

    private fun checkRequiredInfo(): Boolean{
        return capitalTxt.text.toString() != "" && durationTxt.text.toString() != "" && rateTxt.text.toString() != ""
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
