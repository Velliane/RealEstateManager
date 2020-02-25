package com.openclassrooms.realestatemanager.search

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class TypeEnum(@StringRes val res: Int) {

    ANY(R.string.type_any),
    HOUSE(R.string.type_house),
    APARTMENT(R.string.type_apartment);
}