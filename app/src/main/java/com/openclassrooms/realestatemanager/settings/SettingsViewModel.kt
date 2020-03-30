package com.openclassrooms.realestatemanager.settings

import android.content.Context
import androidx.lifecycle.ViewModel

class SettingsViewModel(private val context: Context): ViewModel() {

    fun getCurrencyList(): List<String> {
        val list = ArrayList<String>()
        for(currency in CurrencyEnum.values()){
            list.add(context.getString(currency.res))
        }
        return list
    }
}