package com.openclassrooms.realestatemanager.property.add_edit

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
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
import com.openclassrooms.realestatemanager.data.AddressHelper
import com.openclassrooms.realestatemanager.data.LinkHelper
import com.openclassrooms.realestatemanager.data.PropertyHelper
import com.openclassrooms.realestatemanager.property.show.MainViewModel
import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.photos.Photo
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.property.show.PhotosAdapter
import com.openclassrooms.realestatemanager.utils.*
import org.threeten.bp.LocalDateTime
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Add or Edit a Property
 * If adding, create an Id with LocalDate.now() and save it in PropertyDatabase and Firestore
 * If editing, get the data from the Database with the Id get from the intent, and update it in PropertyDatabase and Firestore
 */
class EditAddActivity : BaseActivity(), View.OnClickListener {

    /** Views */
    private lateinit var autocompleteType: AppCompatAutoCompleteTextView
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
    private lateinit var mainViewModel: MainViewModel
    /** Property Id from DetailsFragment */
    private var propertyId: String = ""
    private var addressId: String = ""
    /** List of Type for Autocomplete */
    private var typeList: List<String> = ArrayList()
    /** URI of selected image*/
    private lateinit var uriImage: Uri
    /** Bitmap */
    private lateinit var bitmap: Bitmap
    /** Image's description*/
    private lateinit var desImage: String
    /** Image List */
    private var imageList = ArrayList<Photo>()
    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var propertyHelper: PropertyHelper
    private lateinit var addressHelper: AddressHelper
    private lateinit var linkHelper: LinkHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add)
        setFinishOnTouchOutside(false)

        typeList = listOf("Apartment", "House", "Loft")
        propertyHelper = PropertyHelper()
        addressHelper = AddressHelper()
        linkHelper = LinkHelper()
        AndroidThreeTen.init(this)
        bindViews()
        getSaveInstanceState(savedInstanceState)
        photosAdapter = PhotosAdapter(this)
        configureViewModel()

        //-- Get Property id from intent --//
        propertyId = intent.getStringExtra(Constants.PROPERTY_ID)!!
        if (propertyId != "") {
            getDataFromDatabase(propertyId)
            // get address
        }

        //-- Set Autocomplete --//
        val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, typeList)
        autocompleteType.threshold = 1
        autocompleteType.setAdapter(adapter)
    }

    /**
     * Get data from the PropertyDatabase if the propertyId is not equals to 0
     * @param id The id of the property
     */
    private fun getDataFromDatabase(id: String) {
        mainViewModel.getPropertyFromId(id).observe(this, Observer<Property> {
            updateViewsWithRoomData(it)
        })
        mainViewModel.getAddressOfOneProperty(id).observe(this, Observer<Address> {
            updateAddressWithRoomData(it)
        })
    }

    /**
     * When click on the FAB
     */
    override fun onClick(v: View?) {
        when (v) {
            saveBtn -> {
                if (propertyId == "") {
                    propertyId = UUID.randomUUID().toString()
                    addressId = UUID.randomUUID().toString()
                }else{
                    mainViewModel.getAddressOfOneProperty(propertyId).observe(this, Observer {
                        addressId = it.id_address
                    })
                }
                //-- Save Property and Address in Room --//
                savePropertyAndAddressInRoom()
                savePropertyAndAddressInFirestore()

                //-- Manage image --//
                saveImageInExternalStorageAndFirestore()

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

    //-- MANAGE PROPERTY AND ADDRESS --//
    private fun savePropertyAndAddressInRoom(){
        val property = Property(propertyId, autocompleteType.text.toString(), price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(), numberBedrooms.text.toString().toInt(), numberBathrooms.text.toString().toInt(), description.text.toString(), true, parseLocalDateTimeToString(LocalDateTime.now()))
        mainViewModel.addProperty(property)
        val address = Address(addressId, number.text.toString().toInt(), street.text.toString(), zipCode.text.toString(), city.text.toString(), country.text.toString(), propertyId)
        mainViewModel.addAddress(address)

    }

    private fun savePropertyAndAddressInFirestore(){
        propertyHelper.createProperty(propertyId, autocompleteType.text.toString(), price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(), 0, 0, description.text.toString(), true, parseLocalDateTimeToString(LocalDateTime.now()))
        addressHelper.createAddress(addressId, number.text.toString().toInt(), street.text.toString(), zipCode.text.toString(), city.text.toString(), country.text.toString(), propertyId)
        val map = HashMap<String, List<String>>()
        map["property_id"] = listOf(propertyId)
        linkHelper.addLink(addressId, map)
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
        val photo = Photo()
        desImage = addPhotoTxt.text.toString()
        if (uriImage != null && desImage != "") {
            photo.uri = uriImage
            photo.description = desImage

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

    private fun saveImageInExternalStorageAndFirestore(){
        if (checkPermission()) {
            if (imageList.isNotEmpty()) {
                for (image in imageList) {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image.uri)
                    mainViewModel.savePhotos(bitmap, propertyId,  image.description!!, uriImage)
                }
            } else {
                requestPermission()
                if (imageList.isNotEmpty()) {
                    for (image in imageList) {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image.uri)
                        mainViewModel.savePhotos(bitmap, propertyId,  image.description!!, uriImage)
                    }
                }
            }
        }
    }

    //-- CONFIGURATION --//
    private fun configureViewModel() {
        val propertyViewModelFactory = Injection.provideMainViewModelFactory(this)
        mainViewModel = ViewModelProviders.of(this, propertyViewModelFactory).get(MainViewModel::class.java)
      }

    private fun bindViews() {
        autocompleteType = findViewById(R.id.edit_type)
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