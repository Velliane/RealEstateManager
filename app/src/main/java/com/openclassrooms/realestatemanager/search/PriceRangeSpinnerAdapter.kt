package com.openclassrooms.realestatemanager.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.openclassrooms.realestatemanager.R

class PriceRangeSpinnerAdapter(context: Context, list: List<PriceRangeEnum>) : ArrayAdapter<PriceRangeEnum>(context, R.layout.spinner_item, list){


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    private fun getCustomView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.spinner_item, parent, false)

        val text = view.findViewById<TextView>(R.id.spinner_item_name)
        val priceRange = getItem(position)

        text.text = priceRange!!.priceRange

        return view
    }
}