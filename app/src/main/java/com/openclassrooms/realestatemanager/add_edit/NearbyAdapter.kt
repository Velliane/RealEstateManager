package com.openclassrooms.realestatemanager.add_edit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.search.NearbyEnum

class NearbyAdapter (context: Context, list: List<NearbyEnum>) : ArrayAdapter<NearbyEnum>(context, R.layout.spinner_item, list){


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    private fun getCustomView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.spinner_item, parent, false)

        val text = view.findViewById<TextView>(R.id.spinner_item_name)
        val nearbyEnum = getItem(position)
        text.text = context.getString(nearbyEnum!!.res)

        return view
    }
}