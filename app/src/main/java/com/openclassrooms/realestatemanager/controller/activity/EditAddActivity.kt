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
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.view_model.injections.Injection
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.setAddressToString
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel
import org.threeten.bp.LocalDateTime

/**
 * Add or Edit a Property
 * If adding, create an Id with LocalDate.now() and save it in PropertyDatabase and Firestore
 * If editing, get the data from the Database with the Id get from the intent, and update it in PropertyDatabase and Firestore
 */
class EditAddActivity : AppCompatActivity(), View.OnClickListener {

    /** AutoCompleteTextView Type */
    private lateinit var autocompleteType: AppCompatAutoCompleteTextView
    /** TextInputEditText */
    private lateinit var price: TextInputEditText
    private lateinit var surface: TextInputEditText
    private lateinit var rooms: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var numberBedrooms: TextInputEditText
    private lateinit var numberBathrooms: TextInputEditText
    private lateinit var number: TextInputEditText
    private lateinit var street: TextInputEditText
    private lateinit var zipCode: TextInputEditText
    private lateinit var city: TextInputEditText
    private lateinit var country: TextInputEditText
    /** FAB */
    private lateinit var saveBtn: FloatingActionButton
    /** ViewModel */
    private lateinit var propertyViewModel: PropertyViewModel
    /** Constraint Layout */
    private lateinit var layout: ConstraintLayout
    /** Property Id from DetailsFragment */
    private var propertyId: String = ""
    /** List of Type for Autocomplete */
    private var typeList: List<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add)
        setFinishOnTouchOutside(false)

        typeList= listOf(getString(R.string.type_apartment), getString(R.string.type_house))

        AndroidThreeTen.init(this)
        bindViews()
        getSaveInstanceState(savedInstanceState)
        configureViewModel()

        //-- Get Property id from intent --//
        propertyId = intent.getStringExtra(Constants.PROPERTY_ID)
        if (propertyId != "") {
            getDataFromDatabase(propertyId)
            //-- If it doesn't exist, create it with LocalDate --//
        } else {
            propertyId = LocalDateTime.now().withNano(0).toString()
        }

        //-- Set Autocomplete --//
        val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, typeList)
        autocompleteType.threshold = 1
        autocompleteType.setAdapter(adapter)
    }

    /**
     * Get data from the PropertyDatabase if the propertyId is not equals to 0
     * @param id The id of the property
     */
    private fun getDataFromDatabase(id: String) {
        propertyViewModel.getPropertyFromId(id).observe(this, Observer<Property> {
            updateViewsWithRoomData(it)
        })
    }

    /**
     * When click on the FAB
     */
    override fun onClick(v: View?) {
        when (v) {
            saveBtn -> {
                val address = Address(0, number.text.toString().toInt(), street.text.toString(), zipCode.text.toString(), city.text.toString(), country.text.toString(), propertyId)
                val property = Property(propertyId, autocompleteType.text.toString(), price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(), null, null, description.text.toString(), setAddressToString(address), true)

                propertyViewModel.addProperty(property)
                propertyViewModel.addAddress(address)

                createProperty(propertyId, autocompleteType.text.toString(), price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(), 0, 0, description.text.toString(), true, setAddressToString(address))
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

    private fun bindViews() {
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
        number = findViewById(R.id.edit_address_number)
        street = findViewById(R.id.edit_address_street)
        zipCode = findViewById(R.id.edit_address_zip_code)
        city = findViewById(R.id.edit_address_city)
        country = findViewById(R.id.edit_address_country)
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

    /**
     * Get the SaveInstance in case of the screen's orientation change
     */
    private fun getSaveInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            setText(price, Constants.KEY_TEXT_PRICE, savedInstanceState)
            setText(surface, Constants.KEY_TEXT_SURFACE, savedInstanceState)
            setText(rooms, Constants.KEY_TEXT_ROOMS, savedInstanceState)
            setText(description, Constants.KEY_TEXT_DESCRIPTION, savedInstanceState)
            setText(numberBedrooms, Constants.KEY_TEXT_BEDROOMS, savedInstanceState)
            setText(numberBathrooms, Constants.KEY_TEXT_BATHROOMS, savedInstanceState)
        }
    }

    private fun setText(view: TextView, key: String, savedInstanceState: Bundle?) {
        val text = savedInstanceState?.getCharSequence(key)
        view.text = text
    }

    //-- UPDATE VIEWS --//
    /**
     * Update the views with the data get from PropertyDatabase
     */
    private fun updateViewsWithRoomData(property: Property) {
        price.setText(property.price.toString())
        surface.setText(property.surface.toString())
        rooms.setText(property.rooms_nbr.toString())
        description.setText(property.description)
        numberBedrooms.setText(property.bed_nbr.toString())
        numberBathrooms.setText(property.bath_nbr.toString())
    }

}