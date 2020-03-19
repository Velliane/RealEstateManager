package com.openclassrooms.realestatemanager.search

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class TypeEnum(@StringRes val res: Int) {

    HOUSE(R.string.type_house),
    APARTMENT(R.string.type_apartment),
    DUPLEX(R.string.type_duplex),
    LOFT(R.string.type_loft);
}