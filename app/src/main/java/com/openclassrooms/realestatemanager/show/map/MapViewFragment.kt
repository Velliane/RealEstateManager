package com.openclassrooms.realestatemanager.show.map

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.material.button.MaterialButton
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.show.BaseFragment
import com.openclassrooms.realestatemanager.show.detail.DetailsFragment
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.getScreenOrientation
import com.openclassrooms.realestatemanager.utils.setAddressToString

class MapViewFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener {

    companion object {
        fun newInstance(): MapViewFragment {
            return MapViewFragment()
        }
    }

    /** GoogleMap */
    private var googleMap: GoogleMap? = null
    /** FusedLocation */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    /** ViewModel */
    private lateinit var mapViewModel: MapViewModel
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences
    /** Reset Button */
    private lateinit var resetBtn: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_view, container, false)

        resetBtn = view.findViewById(R.id.reset_research)
        resetBtn.setOnClickListener(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Places.initialize(requireActivity(), context!!.getString(R.string.api_key_google))
        sharedPreferences = activity!!.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mapViewModel = configurePropertyViewModel()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fragment = childFragmentManager.findFragmentById(R.id.map_fragment)
        (fragment as SupportMapFragment).getMapAsync(this)
    }

    /**
     * Show the GoogleMap
     */
    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        googleMap!!.uiSettings?.isZoomControlsEnabled = true
        googleMap!!.setOnMarkerClickListener(this)
        googleMap!!.setOnInfoWindowClickListener(this)

        //-- Check if permissions are granted for FINE_LOCATION or request it --//
        if (checkLocationPermissions()) {
            googleMap?.isMyLocationEnabled = true
            getUserLocation()
        }
    }

    private fun getUserLocation() {
        //-- Create a LocationRequest --//
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000
        locationRequest.smallestDisplacement = 50F
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY


        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                val lastLocation = LatLng(locationResult!!.lastLocation.latitude, locationResult.lastLocation.longitude)

                googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15F))
                val markerOptions = MarkerOptions().position(lastLocation).title("Test")
                googleMap!!.addMarker(markerOptions)

                getListOfProperty()
            }
        }, null)
    }

    /**
     * Get list of properties and add markers to Map
     */
    private fun getListOfProperty() {
        mapViewModel.propertiesLiveData.observe(this, Observer { list ->
            list?.let {
               for(property in list) {
                   property.address?.let { it1 ->
                       mapViewModel.getLatLng(it1, "country:FR", context!!.resources.getString(R.string.api_key_google)).observe(this, Observer {
                           val result = it.results!![0]
                           val location = LatLng(result.geometry!!.location!!.lat!!, result.geometry!!.location!!.lng!!)
                           val txt = setAddressToString(property.address)
                           val markerOptions = MarkerOptions().position(location).title(property.type).snippet("${txt}, price:${property.price}")
                           googleMap!!.addMarker(markerOptions).tag = property.propertyId
                       })
                   }

               }
           }
        })
   }

    /**
     * Refresh list of properties according to search query
     */
    fun refreshQuery(querySearch: String) {
        mapViewModel.searchInDatabase(querySearch)
        mapViewModel.resetBtnLiveData.observe(this, Observer {
            resetBtn.visibility = it
        })
    }


    override fun onMarkerClick(marker: Marker?): Boolean {
        return false
    }

    override fun onInfoWindowClick(p0: Marker?) {
        val fragment = DetailsFragment.newInstance()
        sharedPreferences.edit().putString(Constants.PREF_ID_PROPERTY, p0!!.tag.toString()).apply()
        if (!getScreenOrientation(activity!!.resources.configuration.orientation)) {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.container_fragment_list, fragment).commit()
        } else {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.container_fragment_details, fragment).commit()
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            resetBtn -> {
                mapViewModel.reset()
                mapViewModel.resetBtnLiveData.observe(this, Observer {
                    resetBtn.visibility = it
                })
            }
        }
    }

}