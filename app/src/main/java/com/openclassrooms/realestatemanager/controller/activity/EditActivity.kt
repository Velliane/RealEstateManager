package com.openclassrooms.realestatemanager.controller.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel

class EditActivity: AppCompatActivity(), View.OnClickListener {

    /** AutoCompleteTextView Type */
    private lateinit var autocompleteType: AppCompatAutoCompleteTextView
    /** TextInputEditText */
    private lateinit var price: TextInputEditText
    private lateinit var surface: TextInputEditText
    private lateinit var rooms: TextInputEditText
    private lateinit var description: TextInputEditText
    /** FAB */
    private lateinit var save_btn: FloatingActionButton
    /** ViewModel */
    private lateinit var propertyViewModel: PropertyViewModel

    private val list: List<String> = listOf("Appartment", "House")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setFinishOnTouchOutside(false)

        bindViews()
        getSaveInstanceState(savedInstanceState)

        configureViewModel()

        val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, list)
        autocompleteType.setThreshold(1)
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

        save_btn = findViewById(R.id.edit_save_btn)
        save_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            save_btn -> {
                val property = Property(0, autocompleteType.text.toString(), price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(), null, null, description.text.toString())
                propertyViewModel.addProperty(property)
            }
        }
    }

    //-- Configuration --//

    private fun configureViewModel() {
        val viewModelFactory = Injection.provideViewModelFactory(this)
        propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel::class.java)
    }

}