package com.openclassrooms.realestatemanager.controller.activity

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import org.w3c.dom.Text

class EditActivity: Activity() {

    /** AutoCompleteTextView Type */
    private lateinit var autocompleteType: AppCompatAutoCompleteTextView
    /** TextInputEditText */
    private lateinit var price: TextInputEditText
    private lateinit var surface: TextInputEditText
    private lateinit var rooms: TextInputEditText
    private lateinit var description: TextInputEditText

    private val list: List<String> = listOf("Appartment", "House")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setFinishOnTouchOutside(false)

        bindViews()
        getSaveInstanceState(savedInstanceState)


        val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, list)
        autocompleteType.setThreshold(1)
        autocompleteType.setAdapter(adapter)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putCharSequence("KEY_TEXT_PRICE", price.text)
        outState?.putCharSequence("KEY_TEXT_SURFACE", surface.text)
        outState?.putCharSequence("KEY_TEXT_ROOMS", rooms.text)
        outState?.putCharSequence("KEY_TEXT_DESCRIPTION", description.text)
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
    }

}