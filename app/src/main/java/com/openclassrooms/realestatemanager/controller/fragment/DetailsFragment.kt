package com.openclassrooms.realestatemanager.controller.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controller.activity.EditAddActivity
import com.openclassrooms.realestatemanager.view_model.injections.Injection
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel

/**
 * Show the information of the property selected in the ListFragment
 */
class DetailsFragment: Fragment() {

    /** ViewModel */
    private lateinit var propertyViewModel: PropertyViewModel

    /** View */
    private lateinit var descrption: TextView
    private lateinit var surface: TextView
    private lateinit var nbrRooms: TextView
    private lateinit var nrbBedrooms: TextView
    private lateinit var nbrBathrooms: TextView

    /** Property id */
    private var propertyId: String = ""

    companion object{

        fun newInstance(id: String): DetailsFragment{
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putString(Constants.PROPERTY_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        setHasOptionsMenu(true)

        propertyId = arguments?.getString(Constants.PROPERTY_ID)!!
        //TODO() get from shared pref

        bindViews(view)
        configureViewModel()
        getPropertyFromId(propertyId)

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
        nrbBedrooms = view.findViewById(R.id.details_bedrooms)
        nbrBathrooms = view.findViewById(R.id.details_bathrooms)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.toolbar_menu_modify).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.toolbar_menu_modify -> {
                val intent = Intent(context, EditAddActivity::class.java)
                intent.putExtra(Constants.PROPERTY_ID, propertyId)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //-- Update views with property's details --//
    /**
     * Get the property from the PropertyDatabase
     */
    private fun getPropertyFromId(id: String){
        propertyViewModel.getPropertyFromId(id).observe(this, Observer<Property> {
            updateViews(it)
        })
    }

    /**
     * Update the views with the data found
     */
    private fun updateViews(property: Property){
        descrption.text = property.description
        surface.text = property.surface.toString()
        nbrRooms.text = property.rooms_nbr.toString()
        nrbBedrooms.text = property.bed_nbr.toString()
        nbrBathrooms.text = property.bath_nbr.toString()
    }
}