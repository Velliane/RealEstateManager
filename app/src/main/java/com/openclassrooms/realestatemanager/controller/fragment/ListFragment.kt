package com.openclassrooms.realestatemanager.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ListAdapter
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel

class ListFragment: Fragment(), ListAdapter.OnItemClickListener {

    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView
    /** ViewModel */
    private lateinit var propertyViewModel: PropertyViewModel
    /** RecyclerView Adapter */
    private lateinit var adapter: ListAdapter

    companion object {
        fun newInstance(): ListFragment {
            return ListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView = view.findViewById(R.id.fragment_list_recycler_view)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL))
        adapter = context?.let { ListAdapter(it, this) }!!

        configureViewModel()
        getListOfProperty()

        return view
    }

    /**
     * Observe the LiveData from PropertyViewModel to get the list of all the properties saved in the RoomDatabase
     */
    private fun getListOfProperty() {
        propertyViewModel.getAllProperty().observe(this, Observer<List<Property>> {
            updateView(it)
        })
    }

    /**
     * Update the data of the RecyclerView with the list of properties
     */
    private fun updateView(properties: List<Property>) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter.setData(properties)
        adapter.notifyDataSetChanged()
    }


    //-- CONFIGURATION --//
    private fun configureViewModel() {
        val viewModelFactory = context?.let { Injection.provideViewModelFactory(it) }
        propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel::class.java)
    }


    /**
     * When click on an item of the RecyclerView
     */
    override fun onItemClicked(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}