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
        val listOfPhoto = ArrayList<Photo>()
        val photo1 = Photo(Uri.parse("025/image.fr"), "Salon séjour avec cheminée")
        val photo2 = Photo(Uri.parse("054/imagefr"), "Grande chambre")
        listOfPhoto.add(photo1)
        listOfPhoto.add(photo2)
        return listOfPhoto
    }
}