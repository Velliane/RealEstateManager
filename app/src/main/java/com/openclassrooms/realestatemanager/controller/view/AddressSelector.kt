package com.openclassrooms.realestatemanager.controller.view

import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import java.lang.StringBuilder

class AddressSelector(context: Context) : ConstraintLayout(context) {

    var number: TextInputEditText? = findViewById(R.id.edit_address_number)
    var street: TextInputEditText? = findViewById(R.id.edit_address_street)
    var zipCode: TextInputEditText?  = findViewById(R.id.edit_address_zip_code)
    var city: TextInputEditText? = findViewById(R.id.edit_address_city)
    var country: TextInputEditText? = findViewById(R.id.edit_address_country)

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_address, this, true)

    }


    open fun getAdress(): String{
        val sb = StringBuilder("Location : ")
        sb.append(number?.text)
        sb.append(street?.text)
        sb.append(zipCode?.text)
        sb.append(city?.text)
        sb.append(country?.text)
        return sb.toString()
    }

    open fun setAddress(numberSaved: Int, streetSaved: String, zipCodeSaved: String?, citySaved: String?, countrySaved: String){
        number?.setText(numberSaved)
        street?.setText(streetSaved)
        zipCode?.setText(zipCodeSaved)
        city?.setText(citySaved)
        country?.setText(countrySaved)
    }
}