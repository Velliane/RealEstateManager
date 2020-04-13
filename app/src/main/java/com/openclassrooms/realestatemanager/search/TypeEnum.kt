package com.openclassrooms.realestatemanager.search

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class TypeEnum(@StringRes val res: Int, val id: Int){

    HOUSE(R.string.type_house, 0),
    APARTMENT(R.string.type_apartment, 1),
    DUPLEX(R.string.type_duplex, 2),
    LOFT(R.string.type_loft, 3),
    VILLA(R.string.type_villa, 4)

}



