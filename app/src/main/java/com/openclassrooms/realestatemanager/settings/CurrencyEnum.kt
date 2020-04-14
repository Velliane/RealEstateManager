package com.openclassrooms.realestatemanager.settings

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class CurrencyEnum(@StringRes val res: Int) {
    DOLLAR(R.string.settings_dollar),
    EURO(R.string.settings_euro)
}