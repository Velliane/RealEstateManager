package com.openclassrooms.realestatemanager.search

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.constructQueryResearch
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var spinnerPrice: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var rangeRooms: CrystalRangeSeekbar
    private lateinit var roomsPreview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        spinnerPrice = findViewById(R.id.search_spinner_price)
        spinnerPrice.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getPriceRangeList())
        spinnerType = findViewById(R.id.search_spinner_type)
        spinnerType.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getTypesList())

        rangeRooms = findViewById(R.id.search_rooms_seek_bar)
        roomsPreview = findViewById(R.id.rooms_view)
        rangeRooms.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            val text = getString(R.string.search_preview_rooms, minValue.toString(), maxValue.toString())
            roomsPreview.text = text
        }

        search_button.setOnClickListener(this)

    }

    private fun getTypesList(): List<String> {
        val list = ArrayList<String>()
        for (type in TypeEnum.values()) {
            val text = getString(type.res)
            list.add(text)
        }
        return list
    }

    private fun getPriceRangeList(): List<String> {
        val list = ArrayList<String>()
        for (price in PriceRangeEnum.values()) {
            val text = price.priceRange
            list.add(text)
        }
        return list
    }

    override fun onClick(item: View?) {
        if (item == search_button) {
            val priceRange = ArrayList<Int>()
            val priceEnum: PriceRangeEnum = PriceRangeEnum.getPriceRangeEnumByValue(spinnerPrice.selectedItem.toString())
            priceRange.add(priceEnum.minValue)
            priceRange.add(priceEnum.maxValue)

            val query = constructQueryResearch(priceRange)
            Log.d("QUERY", query.toString())
//            val propertyViewModelFactory = Injection.providePropertyViewModelFactory(this)
//            val propertyViewModel = ViewModelProviders.of(this, propertyViewModelFactory).get(PropertyViewModel::class.java)
//            propertyViewModel.searchInDatabase(query).observe(this, Observer {
//                val intent = Intent(this, MainActivity::class.java)
//                val bundle = Bundle()
//                bundle.putParcelableArrayList("List property", it as java.util.ArrayList<out Parcelable>)
//                startActivityForResult(intent, 2, bundle)
            }
        }



}
