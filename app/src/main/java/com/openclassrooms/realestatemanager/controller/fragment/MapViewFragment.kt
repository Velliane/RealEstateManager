package com.openclassrooms.realestatemanager.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.openclassrooms.realestatemanager.R
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_view, container, false)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Places.initialize(requireActivity(), context!!.getString(R.string.api_key_google))

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
            }
        }, null)
    }
}