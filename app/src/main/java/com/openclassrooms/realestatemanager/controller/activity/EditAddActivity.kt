package com.openclassrooms.realestatemanager.controller.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.threetenabp.AndroidThreeTen
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.api.createProperty
import com.openclassrooms.realestatemanager.view_model.injections.Injection
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel
import org.threeten.bp.LocalDateTime

/**
 * Add or Edit a Property
 */
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
    /** Property Id from DetailsFragment */
    private var propertyId: String = ""

    private val list: List<String> = listOf("Apartment", "House")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add)
        setFinishOnTouchOutside(false)

        AndroidThreeTen.init(this)
        bindViews()
        getSaveInstanceState(savedInstanceState)
        configureViewModel()
        //-- Get Property id from intent --//
        propertyId = intent.getStringExtra(Constants.PROPERTY_ID)
        if(propertyId != ""){
            getDataFromDatabase(propertyId)
        }else{
            propertyId = LocalDateTime.now().withNano(0).toString()
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, list)
        autocompleteType.threshold = 1
        autocompleteType.setAdapter(adapter)
    }

    /**
     * Get data from the PropertyDatabase if the propertyId is not equals to 0
     * @param id The id of the property
     */
    private fun getDataFromDatabase(id: String){
        propertyViewModel.getPropertyFromId(id).observe(this, Observer<Property> {
            updateViewsWithRoomData(it)
        })
    }

    /**
     * When click on the FAB
     */
    override fun onClick(v: View?) {
        when (v){
            saveBtn -> {
                val property = Property(propertyId, autocompleteType.text.toString(), price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(), null, null, description.text.toString(), true)
                propertyViewModel.addProperty(property)
                createProperty(propertyId, autocompleteType.text.toString(), price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(), 0, 0, description.text.toString(), true)
                Snackbar.make(layout, "Save complete", Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //-- CONFIGURATION --//
    private fun configureViewModel() {
        val viewModelFactory = Injection.providePropertyViewModelFactory(this)
        propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel::class.java)
    }

    private fun bindViews(){
        autocompleteType = findViewById(R.id.edit_type)
        price = findViewById(R.id.edit_price)
        surface = findViewById(R.id.edit_surface)
        rooms = findViewById(R.id.edit_rooms)
        description = findViewById(R.id.edit_description)
        numberBathrooms = findViewById(R.id.edit_bathrooms)
        numberBedrooms = findViewById(R.id.edit_bedrooms)
        layout = findViewById(R.id.edit_container)
        saveBtn = findViewById(R.id.edit_save_btn)
        saveBtn.setOnClickListener(this)
    }

    //-- SAVE INSTANCE --//
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(Constants.KEY_TEXT_PRICE, price.text)
        outState.putCharSequence(Constants.KEY_TEXT_SURFACE, surface.text)
        outState.putCharSequence(Constants.KEY_TEXT_ROOMS, rooms.text)
        outState.putCharSequence(Constants.KEY_TEXT_DESCRIPTION, description.text)
        outState.putCharSequence(Constants.KEY_TEXT_BEDROOMS, numberBedrooms.text)
        outState.putCharSequence(Constants.KEY_TEXT_BATHROOMS, numberBathrooms.text)
    }

    private fun getSaveInstanceState(savedInstanceState: Bundle?){
        if(savedInstanceState != null){
            setText(price, Constants.KEY_TEXT_PRICE, savedInstanceState)
            setText(surface, Constants.KEY_TEXT_SURFACE, savedInstanceState)
            setText(rooms, Constants.KEY_TEXT_ROOMS, savedInstanceState)
            setText(description, Constants.KEY_TEXT_DESCRIPTION, savedInstanceState)
            setText(numberBedrooms, Constants.KEY_TEXT_BEDROOMS, savedInstanceState)
            setText(numberBathrooms, Constants.KEY_TEXT_BATHROOMS, savedInstanceState)
        }
    }

    //-- UPDATE VIEWS --//
    private fun setText(view: TextView, key: String, savedInstanceState: Bundle?){
        val text = savedInstanceState?.getCharSequence(key)
        view.text = text
    }

    private fun updateViewsWithRoomData(property: Property){
        price.setText(property.price.toString())
        surface.setText(property.surface.toString())
        rooms.setText(property.rooms_nbr.toString())
        description.setText(property.description)
        numberBedrooms.setText(property.bed_nbr.toString())
        numberBathrooms.setText(property.bath_nbr.toString())
    }

}