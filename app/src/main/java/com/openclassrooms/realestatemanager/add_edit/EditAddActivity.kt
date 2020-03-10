package com.openclassrooms.realestatemanager.add_edit

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.threetenabp.AndroidThreeTen
import com.openclassrooms.realestatemanager.BaseActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.search.TypeEnum
import com.openclassrooms.realestatemanager.search.TypeEnumSpinnerAdapter
import com.openclassrooms.realestatemanager.show.detail.PhotosAdapter
import com.openclassrooms.realestatemanager.utils.*
import org.threeten.bp.LocalDateTime
import pub.devrel.easypermissions.EasyPermissions
import kotlin.collections.ArrayList

/**
 * Add or Edit a Property
 * If adding, create an Id with LocalDate.now() and save it in PropertyDatabase and Firestore
 * If editing, get the data from the Database with the Id get from the intent, and update it in PropertyDatabase and Firestore
 */
class EditAddActivity : BaseActivity(), View.OnClickListener {

    /** Views */
    private lateinit var spinnerType: Spinner
    private lateinit var price: TextInputEditText
    private lateinit var surface: TextInputEditText
    private lateinit var rooms: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var numberBedrooms: TextInputEditText
    private lateinit var numberBathrooms: TextInputEditText
    private lateinit var number: TextInputEditText
    private lateinit var street: TextInputEditText
    private lateinit var zipCode: TextInputEditText
    private lateinit var city: TextInputEditText
    private lateinit var country: TextInputEditText
    private lateinit var layout: ConstraintLayout
    private lateinit var saveBtn: FloatingActionButton
    private lateinit var addPhotoImg: ImageView
    private lateinit var addPhotoTxt: EditText
    private lateinit var addPhotoBtn: MaterialButton

    /** ViewModel */
    private lateinit var editDataViewModel: EditDataViewModel
    /** Property Id from DetailsFragment */
    private var propertyId: String = ""
    /** List of Type for Autocomplete */
    private var typeList: List<TypeEnum> = ArrayList()
    /** URI of selected image*/
    private lateinit var uriImage: Uri
    /** Image's description*/
    private lateinit var desImage: String
    /** Image List */
    private var imageList = ArrayList<Photo>()
    /** RecyclerView and Adapter */
    private lateinit var recyclerView: RecyclerView
    private var photosAdapter = PhotosAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add)
        setFinishOnTouchOutside(false)
        AndroidThreeTen.init(this)
        configureViewModel()


        typeList = editDataViewModel.getTypesList()
        bindViews()
        getSaveInstanceState(savedInstanceState)


        //-- Get Property id from intent --//
        propertyId = intent.getStringExtra(Constants.PROPERTY_ID)!!
        if (propertyId != "") {
            getDataFromDatabase(propertyId)
        }

        //-- Check permission for Write to External Storage --//
        if(!checkPermission()){ requestPermission() }


        //-- Set TypeEnumSpinner Adapter --//
        val adapter = TypeEnumSpinnerAdapter(this, typeList)
        spinnerType.adapter = adapter
    }

    /**
     * Get data from the PropertyDatabase if the propertyId is not equals to 0
     * @param id The id of the property
     */
    private fun getDataFromDatabase(id: String) {
        editDataViewModel.getPropertyFromId(id)
        editDataViewModel.propertyLiveData.observe(this, Observer<Property> {
            updateViewsWithRoomData(it)
        })
        editDataViewModel.getAddressOfOneProperty(id)
        editDataViewModel.addressLiveData.observe(this, Observer<Address> {
            updateAddressWithRoomData(it)
        })
    }

    /**
     * When click on the FAB
     */
    override fun onClick(v: View?) {
        when (v) {
            saveBtn -> {
                val property = Property(propertyId, spinnerType.selectedItem.toString(), price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(), numberBedrooms.text.toString().toInt(), numberBathrooms.text.toString().toInt(), description.text.toString(), true, parseLocalDateTimeToString(LocalDateTime.now()))
                editDataViewModel.save(property.id_property, property,number.text.toString().toInt(), street.text.toString(), zipCode.text.toString(), city.text.toString(), country.text.toString(), imageList)
                sendNotification()
                Snackbar.make(layout, "Save complete", Snackbar.LENGTH_SHORT).show()
                finish()
            }
            addPhotoImg -> {
                //-- Ask permission and let user select an image from his phone --//
                if (!EasyPermissions.hasPermissions(this, Constants.PERMS)) {
                    EasyPermissions.requestPermissions(this, "Permission", Constants.RC_PERMISSION_FILES_STORAGE, Constants.PERMS)
                    return
                }
                selectAnImageFromThePhone()
            }

            addPhotoBtn -> addImageToListAndShowIt()
        }
    }

    //-- NOTIFICATION --//
    private fun sendNotification(){
        val data = Data.Builder().putString(Constants.DATA_USER_NAME, getCurrentUser().displayName).build()
        NotificationWorker.configureNotification(data)
    }

    //-- MANAGE IMAGE --//
    private fun selectAnImageFromThePhone() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, Constants.RC_CHOOSE_IMAGE)
    }

    private fun addImageToListAndShowIt(){
        desImage = addPhotoTxt.text.toString()
        if (uriImage != null && desImage != "") {
            val photo = Photo(uriImage, desImage)
            imageList.add(photo)
            recyclerView.adapter = photosAdapter
            photosAdapter.setData(imageList)
            photosAdapter.notifyDataSetChanged()
        } else if (desImage == "") {
            Snackbar.make(layout, "Please write a description", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(layout, "Please select an image and write a description", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadImage(requestCode, resultCode, data)
    }

    private fun loadImage(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.RC_CHOOSE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                uriImage = data!!.data!!
                Glide.with(this).load(uriImage).into(addPhotoImg)
                addPhotoBtn.visibility = View.VISIBLE
            } else {
                //TODO error message
            }
        }
    }

    //-- CONFIGURATION --//
    private fun configureViewModel() {
        val editDataViewModelFactory = Injection.provideViewModelFactory(this)
        editDataViewModel = ViewModelProviders.of(this, editDataViewModelFactory).get(EditDataViewModel::class.java)
      }

    private fun bindViews() {
        spinnerType = findViewById(R.id.add_edit_type_spinner)
        price = findViewById(R.id.edit_price)
        surface = findViewById(R.id.edit_surface)
        rooms = findViewById(R.id.edit_rooms)
        description = findViewById(R.id.edit_description)
        numberBathrooms = findViewById(R.id.edit_bathrooms)
        numberBedrooms = findViewById(R.id.edit_bedrooms)
        layout = findViewById(R.id.edit_container)
        saveBtn = findViewById(R.id.edit_save_btn)
        saveBtn.setOnClickListener(this)
        number = findViewById(R.id.edit_address_number)
        street = findViewById(R.id.edit_address_street)
        zipCode = findViewById(R.id.edit_address_zip_code)
        city = findViewById(R.id.edit_address_city)
        country = findViewById(R.id.edit_address_country)
        addPhotoImg = findViewById(R.id.edit_add_photos)
        addPhotoImg.setOnClickListener(this)
        addPhotoTxt = findViewById(R.id.edit_add_photos_txt)
        addPhotoBtn = findViewById(R.id.edit_add_photos_btn)
        addPhotoBtn.setOnClickListener(this)
        recyclerView = findViewById(R.id.edit_list_photos)
    }

    //-- SAVE INSTANCE --//
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(Constants.KEY_TEXT_PRICE, price.text)
        outState.putCharSequence(Constants.KEY_TEXT_SURFACE, surface.text)
        outState.putCharSequence(Constants.KEY_TEXT_ROOMS, rooms.text)
        outState.putCharSequence(Constants.KEY_TEXT_DESCRIPTION, description.text)
        outState.putCharSequence(Constants.KEY_TEXT_BEDROOMS, numberBedrooms.text)
        outState.putCharSequence(Constants.KEY_TEXT_BATHROOMS, numberBathrooms.text)
        outState.putCharSequence(Constants.KEY_TEXT_TYPE, spinnerType.selectedItem.toString())
    }

    /**
     * Get the SaveInstance in case of the screen's orientation change
     */
    private fun getSaveInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            setText(price, Constants.KEY_TEXT_PRICE, savedInstanceState)
            setText(surface, Constants.KEY_TEXT_SURFACE, savedInstanceState)
            setText(rooms, Constants.KEY_TEXT_ROOMS, savedInstanceState)
            setText(description, Constants.KEY_TEXT_DESCRIPTION, savedInstanceState)
            setText(numberBedrooms, Constants.KEY_TEXT_BEDROOMS, savedInstanceState)
            setText(numberBathrooms, Constants.KEY_TEXT_BATHROOMS, savedInstanceState)
        }
    }

    private fun setText(view: TextView, key: String, savedInstanceState: Bundle?) {
        val text = savedInstanceState?.getCharSequence(key)
        view.text = text
    }

    //-- UPDATE VIEWS --//
    /**
     * Update the views with the data get from PropertyDatabase
     */
    private fun updateViewsWithRoomData(property: Property) {
        price.setText(property.price.toString())
        surface.setText(property.surface.toString())
        rooms.setText(property.rooms_nbr.toString())
        description.setText(property.description)
        numberBedrooms.setText(property.bed_nbr.toString())
        numberBathrooms.setText(property.bath_nbr.toString())
    }

    private fun updateAddressWithRoomData(address: Address) {
        number.setText(address.number.toString())
        street.setText(address.street)
        zipCode.setText(address.zip_code)
        city.setText(address.city)
        country.setText(address.country)
    }

    //-- Request WRITE_EXTERNAL_STORAGE permission --//
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), Constants.RC_PERMISSION_LOCATION)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}