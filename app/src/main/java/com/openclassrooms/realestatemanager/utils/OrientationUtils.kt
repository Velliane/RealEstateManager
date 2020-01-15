package com.openclassrooms.realestatemanager.utils

import android.content.res.Configuration

fun getScreenOrientation(orientation: Int): Boolean{
    var isTablet = false
    if(orientation == Configuration.ORIENTATION_PORTRAIT){
        isTablet = false
    }else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
        isTablet = true
    }
    return isTablet
}