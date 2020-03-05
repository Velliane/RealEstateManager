package com.openclassrooms.realestatemanager.show

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.show.detail.DetailsFragment
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.getScreenOrientation

class MapViewFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

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
    private lateinit var mainViewModel: MainViewModel
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_view, container, false)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Places.initialize(requireActivity(), context!!.getString(R.string.api_key_google))
        sharedPreferences = activity!!.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mainViewModel = configurePropertyViewModel()
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

    private fun getListOfProperty() {
        mainViewModel.propertiesLiveData.observe(this, Observer<List<Property>> { list ->
            for (property in list) {
                mainViewModel.getAddressOfOneProperty(property.id_property)
                mainViewModel.addressLiveData.observe(this, Observer<Address> { address ->
                    mainViewModel.getLatLng(address, "country:FR", context!!.resources.getString(R.string.api_key_google)).observe(this, Observer {
                        val result = it.results!![0]
                        val location = LatLng(result.geometry!!.location!!.lat!!, result.geometry!!.location!!.lng!!)
                        val markerOptions = MarkerOptions().position(location).title(property.price.toString())

                        googleMap!!.addMarker(markerOptions).tag = property.id_property

                    })
                })
            }
        })
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val fragment = DetailsFragment.newInstance()
        sharedPreferences.edit().putString(Constants.PREF_ID_PROPERTY, marker!!.tag.toString()).apply()

        if (!getScreenOrientation(activity!!.resources.configuration.orientation)) {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.container_fragment_list, fragment).commit()
        } else {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.container_fragment_details, fragment).commit()
        }
        return true
    }


}