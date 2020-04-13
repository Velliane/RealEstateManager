package com.openclassrooms.realestatemanager.show.detail

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.EditAddActivity
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Photo
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.show.geocode_model.Geocode
import com.openclassrooms.realestatemanager.show.BaseFragment
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection
import com.openclassrooms.realestatemanager.utils.setAddressToString

/**
 * Show the information of the property selected in the ListFragment
 */
class DetailsFragment : BaseFragment(), PhotosAdapter.OnItemClickListener {

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
    private lateinit var container: ConstraintLayout
    private lateinit var noData: TextView
    private lateinit var agentTxt: TextView

    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var viewModel: DetailViewModel

    /** Property id */
    private var propertyId: String = ""

    companion object {

        fun newInstance(): DetailsFragment {
            return DetailsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        setHasOptionsMenu(true)

        sharedPreferences = activity!!.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        propertyId = sharedPreferences.getString(Constants.PREF_ID_PROPERTY, "")!!
        val viewModelFactory = Injection.provideViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)

        bindViews(view)
        if (propertyId != "") {
            if (container != null) {
                container.visibility = View.VISIBLE
            }
            noData.visibility = View.GONE
            photosAdapter = PhotosAdapter(requireContext(), this)
            getPropertyFromId(propertyId)
            getAddressOfProperty(propertyId)
            viewModel.getListOfPhotos(propertyId)
            viewModel.listPhotosLiveData.observe(this, Observer {
                recyclerView.adapter = photosAdapter
                photosAdapter.setData(it)
                photosAdapter.notifyDataSetChanged()
            })
        } else {
            if (container != null) {
                container.visibility = View.GONE
            }
            noData.visibility = View.VISIBLE
        }
        return view
    }

    //-- CONFIGURATION --//

    private fun bindViews(view: View) {
        descrption = view.findViewById(R.id.details_description)
        surface = view.findViewById(R.id.details_surface)
        nbrRooms = view.findViewById(R.id.details_nbr_rooms)
        nrbBedrooms = view.findViewById(R.id.details_bedrooms)
        nbrBathrooms = view.findViewById(R.id.details_bathrooms)
        addressView = view.findViewById(R.id.details_address)
        map = view.findViewById(R.id.details_map)
        recyclerView = view.findViewById(R.id.detail_photos)
        container = view.findViewById(R.id.details_infos_container)
        noData = view.findViewById(R.id.details_no_data)
        agentTxt = view.findViewById(R.id.details_manager)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.toolbar_menu_modify).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_menu_modify -> {
                if(propertyId == "") {
                    Snackbar.make(container, "Please select a place to modify", Snackbar.LENGTH_SHORT).show()
                }else {
                    val intent = Intent(context, EditAddActivity::class.java)
                    intent.putExtra(Constants.PROPERTY_ID, propertyId)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //-- Update views with property's details --//
    /**
     * Get the property from the PropertyDatabase
     */
    private fun getPropertyFromId(id: String) {
        viewModel.getPropertyFromId(id)
        viewModel.propertyLiveData.observe(this, Observer { property ->
            updateGeneralInfo(property)
            viewModel.setAgent(property.agent)
            viewModel.agentLiveData.observe(this, Observer {
                agentTxt.text = getString(R.string.manage_by, it.name)
            })
        })
    }

    private fun getAddressOfProperty(id: String) {
        viewModel.getAddressOfOneProperty(id)
        viewModel.addressLiveData.observe(this, Observer<Address> {
            updateAddress(it)
            setMapsImage(it)
        })
    }

    private fun setMapsImage(address: Address) {
        val key = context!!.getString(R.string.api_key_google)
        viewModel.getLatLng(address, "country:FR", key).observe(this, Observer<Geocode> {
            if (it != null) {
                val lat = it.results!![0].geometry!!.location!!.lat
                val lng = it.results!![0].geometry!!.location!!.lng
                val url = "https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=300x300&maptype=roadmap\n" +
                        "&key=$key&markers=color:blue%7Clabel:S%7C$lat,$lng"
                Glide.with(this).load(url).into(map)
            } else {
                //TODO default image
            }
        })
    }

    /**
     * Update the views with the data found
     */
    private fun updateGeneralInfo(property: Property) {
        descrption.text = property.description
        surface.text = property.surface.toString()
        nbrRooms.text = property.rooms_nbr.toString()
        nrbBedrooms.text = property.bed_nbr.toString()
        nbrBathrooms.text = property.bath_nbr.toString()
    }

    private fun updateAddress(address: Address) {
        addressView.text = setAddressToString(address)
    }

    override fun onItemClicked(photo: Photo, position: Int) {
        //
    }
}