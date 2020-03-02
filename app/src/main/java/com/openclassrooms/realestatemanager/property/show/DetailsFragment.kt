package com.openclassrooms.realestatemanager.property.show

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.property.add_edit.EditAddActivity
import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.property.model.geocode.Geocode
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.setAddressToString

/**
 * Show the information of the property selected in the ListFragment
 */
class DetailsFragment: BaseFragment() {

    /** ViewModel */
    private lateinit var mainViewModel: MainViewModel
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences
    /** View */
    private lateinit var descrption: TextView
    private lateinit var surface: TextView
    private lateinit var nbrRooms: TextView
    private lateinit var nrbBedrooms: TextView
    private lateinit var nbrBathrooms: TextView
    private lateinit var addressView: TextView
    private lateinit var map: ImageView
    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView
    private lateinit var photosAdapter: PhotosAdapter

    /** Property id */
    private var propertyId: String = ""

    companion object{

        fun newInstance(): DetailsFragment {
            val fragment = DetailsFragment()
//            val args = Bundle()
//            args.putString(Constants.PROPERTY_ID, id)
//            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        setHasOptionsMenu(true)

        sharedPreferences = activity!!.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        propertyId = sharedPreferences.getString(Constants.PREF_ID_PROPERTY, "")!!

        bindViews(view)
        mainViewModel = configurePropertyViewModel()
        photosAdapter = PhotosAdapter(requireContext())
        getPropertyFromId(propertyId)
        getAddressOfProperty(propertyId)
        mainViewModel.getListOfPhotos(propertyId)
        mainViewModel.listPhotosLiveData.observe(this, Observer {
            recyclerView.adapter = photosAdapter
            photosAdapter.setData(it)
            photosAdapter.notifyDataSetChanged()
        })

        return view
    }

    //-- CONFIGURATION --//

    private fun bindViews(view: View){
        descrption = view.findViewById(R.id.details_description)
        surface = view.findViewById(R.id.details_surface)
        nbrRooms = view.findViewById(R.id.details_nbr_rooms)
        nrbBedrooms = view.findViewById(R.id.details_bedrooms)
        nbrBathrooms = view.findViewById(R.id.details_bathrooms)
        addressView = view.findViewById(R.id.details_address)
        map = view.findViewById(R.id.details_map)
        recyclerView = view.findViewById(R.id.detail_photos)
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
        mainViewModel.getPropertyFromId(id).observe(this, Observer<Property> {
            updateGeneralInfo(it)
        })
    }

    private fun getAddressOfProperty(id: String){
        mainViewModel.getAddressOfOneProperty(id).observe(this, Observer<Address> {
            updateAddress(it)
            setMapsImage(it)
        })
    }

    private fun setMapsImage(address: Address){
        val key = context!!.getString(R.string.api_key_google)
        mainViewModel.getLatLng(address, "country:FR",key).observe(this, Observer<Geocode> {
            if(it != null) {
                val lat = it.results!![0].geometry!!.location!!.lat
                val lng = it.results!![0].geometry!!.location!!.lng
                val url = "$lat,$lng"
                val url2 = "https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=300x300&maptype=roadmap\n" +
                        "&key=$key&markers=color:blue%7Clabel:S%7C$lat,$lng"
                Glide.with(this).load(url2).into(map)
            }else{
                //TODO default image
            }
        })
    }

    /**
     * Update the views with the data found
     */
    private fun updateGeneralInfo(property: Property){
        descrption.text = property.description
        surface.text = property.surface.toString()
        nbrRooms.text = property.rooms_nbr.toString()
        nrbBedrooms.text = property.bed_nbr.toString()
        nbrBathrooms.text = property.bath_nbr.toString()
    }

    private fun updateAddress(address: Address) {
        addressView.text = setAddressToString(address)
    }
}