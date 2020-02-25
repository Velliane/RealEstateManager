package com.openclassrooms.realestatemanager.property.show

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection
import pub.devrel.easypermissions.EasyPermissions

open class BaseFragment: Fragment() {

    //-- PERMISSIONS--//
    /**
     * Check if permissions are granted. If not, request
     */
    open fun checkPermissions(): Boolean {
        return if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            true
        }else{ //-- Request permissions --//
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), Constants.RC_PERMISSION_LOCATION)
            false
        }
    }

    /**
     * After requesting permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Configure PropertyViewModel
     */
    open fun configurePropertyViewModel(): MainViewModel {
        val viewModelFactory = context?.let { Injection.provideMainViewModelFactory(it) }
        return ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }
}