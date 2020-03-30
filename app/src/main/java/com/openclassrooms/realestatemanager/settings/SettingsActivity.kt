package com.openclassrooms.realestatemanager.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.search.ArrayListStringAdapter
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    /** Currency Spinner */
    private lateinit var currencySPinner: Spinner
    /** ViewModel */
    private lateinit var viewModel: SettingsViewModel
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences
    /** Save Settings Button */
    private lateinit var saveBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel::class.java)
        bindViews()
    }

    private fun bindViews(){
        val toolbar: Toolbar = findViewById(R.id.settings_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Paramètres"
        currencySPinner = findViewById(R.id.settings_spinner_money)
        currencySPinner.adapter = ArrayListStringAdapter(this, viewModel.getCurrencyList())
        val currencySaved = sharedPreferences.getInt(Constants.PREF_CURRENCY, 0)
        currencySPinner.setSelection(currencySaved)
        saveBtn = findViewById(R.id.settings_save_btn)
        saveBtn.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when(view){
            saveBtn -> {
                sharedPreferences.edit().putInt(Constants.PREF_CURRENCY, currencySPinner.selectedItemPosition).apply()
                Snackbar.make(settings_container, "Settings changed successfully", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    //-- CONFIGURE TOOLBAR --//
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.toolbar_menu_add)?.isVisible = false
        menu?.findItem(R.id.toolbar_menu_modify)?.isVisible = false
        menu?.findItem(R.id.toolbar_menu_search)?.isVisible =false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

}
