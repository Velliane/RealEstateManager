package com.openclassrooms.realestatemanager.search

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class NearbyEnum(@StringRes val res: Int) {

    BUS(R.string.nearby_bus),
    SHOP(R.string.nearby_shop),
    SCHOOL(R.string.nearby_school),
    POST(R.string.nearby_post),
    PUBLIC_TRANSPORT(R.string.nearby_public_transport),
    RESTAURANT(R.string.nearby_restaurant),
    UNIVERSITY(R.string.nearby_university);

}