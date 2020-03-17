package com.openclassrooms.realestatemanager.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.show.MainActivity
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var spinnerPrice: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var rangeRooms: CrystalRangeSeekbar
    private lateinit var roomsPreview: TextView

    /** ViewModel */
    private lateinit var searchViewModel: SearchViewModel

    private var roomsMinValue = 1
    private var roomsMaxValue = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        title = getString(R.string.search_button)

        //-- Configure ViewModel --//
        val searchViewModelFactory = Injection.provideSearchViewModel(this)
        searchViewModel = ViewModelProviders.of(this, searchViewModelFactory).get(SearchViewModel::class.java)

        bindViews()
        manageRangeSeekBar()

        search_button.setOnClickListener(this)

    }

    private fun bindViews() {
        //-- Spinners --//
        spinnerPrice = findViewById(R.id.search_spinner_price)
        spinnerPrice.adapter = PriceRangeSpinnerAdapter(this, searchViewModel.getPriceRangeList())
        spinnerType = findViewById(R.id.search_spinner_type)
        spinnerType.adapter = TypeEnumSpinnerAdapter(this, searchViewModel.getTypesList())
        //-- RangeSeekBars --//
        rangeRooms = findViewById(R.id.search_rooms_seek_bar)
        roomsPreview = findViewById(R.id.rooms_view)
    }

    private fun manageRangeSeekBar() {
        rangeRooms.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            roomsMinValue = minValue.toInt()
            roomsMaxValue = maxValue.toInt()
            val text = getString(R.string.search_preview_rooms, minValue.toString(), maxValue.toString())
            roomsPreview.text = text
        }
    }

    override fun onClick(item: View?) {
        if (item == search_button) {
            val query = searchViewModel.searchDatabase(spinnerPrice.selectedItem.toString(), spinnerType.selectedItem.toString(), roomsMinValue, roomsMaxValue)
            val intent = Intent()
            intent.putExtra(Constants.SEARCH_QUERY, query)
            setResult(Constants.RC_SEARCH, intent)
            finish()
        }
    }

}
