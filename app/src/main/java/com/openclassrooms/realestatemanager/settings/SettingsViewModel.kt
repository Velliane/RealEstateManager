package com.openclassrooms.realestatemanager.settings

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel

class SettingsViewModel(private val context: Context): ViewModel() {

    @VisibleForTesting
    fun getCurrencyList(): List<String> {
        val list = ArrayList<String>()
        for(currency in CurrencyEnum.values()){
            list.add(context.getString(currency.res))
        }
        return list
    }
}