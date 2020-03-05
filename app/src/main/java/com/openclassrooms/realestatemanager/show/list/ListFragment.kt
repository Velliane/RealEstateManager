package com.openclassrooms.realestatemanager.show.list

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.show.BaseFragment
import com.openclassrooms.realestatemanager.show.detail.DetailsFragment
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection
import com.openclassrooms.realestatemanager.utils.getScreenOrientation

/**
 * Fragment that show the list of properties saved in the PropertyDatabase
 * When click on an item of the list:
 * - if orientation is portrait : the fragment DetailsFragment replace the LiseFragment with the details of the property clicked
 * - if orientation is landscape : the fragment DetailsFragment is update with the details of the property clicked
 */

class ListFragment: BaseFragment(), ListPropertyAdapter.OnItemClickListener {

    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView
    /** ViewModel */
    private lateinit var mainViewModel: ListViewModel
    /** RecyclerView Adapter */
    private lateinit var adapter: ListPropertyAdapter
    /** No Data TextView */
    private lateinit var noDataTxt: TextView
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        fun newInstance(): ListFragment {
            return ListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView = view.findViewById(R.id.fragment_list_recycler_view)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL))
        recyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        adapter = ListPropertyAdapter(this, context!!)
        noDataTxt = view.findViewById(R.id.fragment_list_no_data)

        sharedPreferences = activity!!.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        val viewModelFactory = Injection.provideViewModelFactory(requireContext())
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(ListViewModel::class.java)
        if(checkExternalStoragePermissions()) {
            getListOfProperty()
        }

        return view
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(!getScreenOrientation(resources.configuration.orientation)){
            menu.findItem(R.id.toolbar_menu_modify).isVisible = false
        }
    }

    /**
     * Observe the LiveData from PropertyViewModel to get the list of all the properties saved in the RoomDatabase
     */
    private fun getListOfProperty() {
        mainViewModel.propertiesLiveData.observe(this, Observer<List<PropertyModelForList>> {
            updateView(it)
        })
    }

    /**
     * Update the data of the RecyclerView with the list of properties
     * List<Property>: the list of Property get from the PropertyDatabase
     */
    private fun updateView(properties: List<PropertyModelForList>?) {
        if(properties == null || properties.isEmpty()){
            recyclerView.visibility = View.INVISIBLE
            noDataTxt.visibility = View.VISIBLE

        }else{
            recyclerView.adapter = adapter
            adapter.setData(properties)
            adapter.notifyDataSetChanged()
        }

    }


    /**
     * When click on an item of the RecyclerView
     */
    override fun onItemClicked(id: String, position: Int) {
        val fragment = DetailsFragment.newInstance()
        sharedPreferences.edit().putString(Constants.PREF_ID_PROPERTY, id).apply()
        adapter.getItemViewType(position)
        adapter.notifyDataSetChanged()

        if(!getScreenOrientation(activity!!.resources.configuration.orientation)){
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.container_fragment_list, fragment).commit()
        }else{
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.container_fragment_details, fragment).commit()
        }

    }

}