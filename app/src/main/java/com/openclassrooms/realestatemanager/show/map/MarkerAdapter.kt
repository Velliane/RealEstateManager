package com.openclassrooms.realestatemanager.show.map

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.openclassrooms.realestatemanager.R

class MarkerAdapter(private val layoutInflater: LayoutInflater): GoogleMap.InfoWindowAdapter {
    @SuppressLint("InflateParams")
    override fun getInfoContents(marker: Marker?): View {
        val view = layoutInflater.inflate(R.layout.marker_custom_layout, null)

        val title = view.findViewById<TextView>(R.id.title)
        val snippet = view.findViewById<TextView>(R.id.snippet)
        title.text = marker?.title
        snippet.text = marker?.snippet

        return view
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }

}