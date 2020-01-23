package com.openclassrooms.realestatemanager.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.view_model.injections.Injection
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel

class DetailsFragment: Fragment() {

    /** ViewModel */
    private lateinit var propertyViewModel: PropertyViewModel

    /** View */
    private lateinit var descrption: TextView
    private lateinit var surface: TextView
    private lateinit var nbrRooms: TextView

    companion object{

        fun newInstance(id: Int): DetailsFragment{
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putInt(Constants.PROPERTY_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        val id: Int = arguments?.getInt(Constants.PROPERTY_ID)!!

        bindViews(view)
        configureViewModel()
        getPropertyFromId(id)


        return view
    }

    //-- CONFIGURATION --//
    private fun configureViewModel() {
        val viewModelFactory = context?.let { Injection.providePropertyViewModelFactory(it) }
        propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel::class.java)
    }

    private fun bindViews(view: View){
        descrption = view.findViewById(R.id.details_description)
        surface = view.findViewById(R.id.details_surface)
        nbrRooms = view.findViewById(R.id.details_nbr_rooms)
    }

    //-- Update views with property's details --//
    private fun getPropertyFromId(id: Int){
        propertyViewModel.getPropertyFromId(id).observe(this, Observer<Property> {
            updateViews(it)
        })
    }

    private fun updateViews(property: Property){
        descrption.text = property.description
        surface.text = property.surface.toString()
        nbrRooms.text = property.rooms_nbr.toString()
    }
}