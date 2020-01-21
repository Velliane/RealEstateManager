package com.openclassrooms.realestatemanager.controller.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controller.view.AddressSelector
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel

class EditAddActivity: AppCompatActivity(), View.OnClickListener {

    /** AutoCompleteTextView Type */
    private lateinit var autocompleteType: AppCompatAutoCompleteTextView
    /** TextInputEditText */
    private lateinit var price: TextInputEditText
    private lateinit var surface: TextInputEditText
    private lateinit var rooms: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var numberBedrooms: TextInputEditText
    private lateinit var numberBathrooms: TextInputEditText
    /** FAB */
    private lateinit var saveBtn: FloatingActionButton
    /** ViewModel */
    private lateinit var propertyViewModel: PropertyViewModel
    /** Constraint Layout */
    private lateinit var layout: ConstraintLayout

    private val list: List<String> = listOf("Appartment", "House")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add)
        setFinishOnTouchOutside(false)

        bindViews()
        getSaveInstanceState(savedInstanceState)
        configureViewModel()

        val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, list)
        autocompleteType.threshold = 1
        autocompleteType.setAdapter(adapter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("KEY_TEXT_PRICE", price.text)
        outState.putCharSequence("KEY_TEXT_SURFACE", surface.text)
        outState.putCharSequence("KEY_TEXT_ROOMS", rooms.text)
        outState.putCharSequence("KEY_TEXT_DESCRIPTION", description.text)
    }

    private fun getSaveInstanceState(savedInstanceState: Bundle?){
        if(savedInstanceState != null){
            val priceTxt = savedInstanceState.getCharSequence("KEY_TEXT_PRICE")
            price.setText(priceTxt)
            val surfaceTxt = savedInstanceState.getCharSequence("KEY_TEXT_SURFACE")
            surface.setText(surfaceTxt)
            val roomsTxt = savedInstanceState.getCharSequence("KEY_TEXT_ROOMS")
            rooms.setText(roomsTxt)
        }
    }

    private fun bindViews(){
        autocompleteType = findViewById(R.id.edit_type)
        price = findViewById(R.id.edit_price)
        surface = findViewById(R.id.edit_surface)
        rooms = findViewById(R.id.edit_rooms)
        description = findViewById(R.id.edit_description)
        layout = findViewById(R.id.edit_container)

        saveBtn = findViewById(R.id.edit_save_btn)
        saveBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            saveBtn -> {
                val property = Property(0, autocompleteType.text.toString(), price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(), null, null, description.text.toString())
                propertyViewModel.addProperty(property)
                Snackbar.make(layout, "Save complete", Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //-- Configuration --//

    private fun configureViewModel() {
        val viewModelFactory = Injection.provideViewModelFactory(this)
        propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel::class.java)
    }

}