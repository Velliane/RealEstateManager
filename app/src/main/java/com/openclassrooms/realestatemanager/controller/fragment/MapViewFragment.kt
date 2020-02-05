package com.openclassrooms.realestatemanager.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel
import java.lang.Exception

class MapViewFragment : BaseFragment(), OnMapReadyCallback {

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
    private lateinit var propertyViewModel: PropertyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_view, container, false)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Places.initialize(requireActivity(), context!!.getString(R.string.api_key_google))

        propertyViewModel = configurePropertyViewModel()
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
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        //-- Check if permissions are granted for FINE_LOCATION or request it --//
        if(checkPermissions()){
            googleMap?.isMyLocationEnabled = true
            getUserLocation()
        }
    }

    private fun getUserLocation(){
        //-- Create a LocationRequest --//
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000
        locationRequest.smallestDisplacement = 50F
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY


        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                val lastLocation= LatLng(locationResult!!.lastLocation.latitude, locationResult.lastLocation.longitude)

                googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15F))
                val markerOptions = MarkerOptions().position(lastLocation).title("Test")
                googleMap!!.addMarker(markerOptions)

                getListOfProperty()
            }
        }, null)
    }

    private fun getListOfProperty(){
        propertyViewModel.getAllProperty().observe(this, Observer<List<Property>> { list ->
            for(property in list){
                propertyViewModel.getAddressOfOnePorperty(property.id_property).observe(this, Observer<Address> {address ->
                    propertyViewModel.getLatLng(address, "country:FR", context!!.resources.getString(R.string.api_key_google)).observe(this, Observer {
                        val result = it.results!![0]
                        val location = LatLng(result.geometry!!.location!!.lat!!, result.geometry!!.location!!.lng!!)
                        val markerOptions = MarkerOptions().position(location).title(property.price.toString())
                        googleMap!!.addMarker(markerOptions)
                    })
                })
            }
        })
    }
}