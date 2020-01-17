package com.openclassrooms.realestatemanager.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Constants

class DetailsFragment: Fragment() {

    companion object{

        fun newInstance(): DetailsFragment{
            val fragment = DetailsFragment()
//            val args = Bundle()
//            args.putInt(Constants.PROPERTY_ID, id)
//            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        return view
    }
}