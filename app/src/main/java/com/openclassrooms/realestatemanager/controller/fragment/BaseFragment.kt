package com.openclassrooms.realestatemanager.controller.fragment

import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    //-- PERMISSIONS--//
    /**
     * Check if permissions are granted. If not, request
     */
    open fun checkPermissions(): Boolean {
        return if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            true
        }else{
            //-- Request permissions --//
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 21)
            false
        }
    }

    /**
     * After requesting permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            21 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission granted", "Permission granted")
                } else {
                    Log.d("Permission denied", "Permission denied")
                }
                return
            }
        }
    }
}