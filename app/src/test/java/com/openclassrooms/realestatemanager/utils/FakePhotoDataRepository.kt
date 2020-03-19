package com.openclassrooms.realestatemanager.utils

import android.graphics.Bitmap
import android.net.Uri
import com.openclassrooms.realestatemanager.add_edit.Photo
import com.openclassrooms.realestatemanager.data.PhotoDataRepository

class FakePhotoDataRepository(): PhotoDataRepository() {

    override fun saveImageToExternalStorage(bitmap: Bitmap, id_property: String, description: String) {
        super.saveImageToExternalStorage(bitmap, id_property, description)
    }

    override fun saveImageToFirebase(uri: Uri, id_property: String, description: String) {
        super.saveImageToFirebase(uri, id_property, description)
    }

    override fun getListOfPhotos(id_property: String): List<Photo>? {
        return super.getListOfPhotos(id_property)
    }
}