package com.openclassrooms.realestatemanager.data

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.realestatemanager.add_edit.Photo
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

open class PhotoDataRepository {

    /**
     * Save image selected by user
     * @param bitmap the image
     * @param id_property the id of the property for the name of the folder
     * @param description the description of the image
     * @return the path of the image
     */
    open fun saveImageToExternalStorage(bitmap: Bitmap, id_property: String, description: String){

        val root = Environment.getExternalStorageDirectory().path + "/RealEstateManager/"
        val dir = File(root)
        if(!dir.exists()) {
            dir.mkdirs()
        }
        val folder = File(root, "$id_property/")
        if(!folder.exists()){
            folder.mkdirs()
        }
        val file = File(folder, "$description.jpg")
        if(file.exists()) file.delete()
        file.createNewFile()
        Log.d("Address file", file.absolutePath)
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    open fun saveImageToFirebase(uri: String, id_property: String, description: String){
        val storageReference = FirebaseStorage.getInstance().getReference("/images/$id_property/$description")
        storageReference.putFile(Uri.parse(uri)).addOnSuccessListener {
        }
    }

    open fun getListOfPhotos(id_property: String): ArrayList<Photo>?{
        val listPhoto = ArrayList<Photo>()
        val photoPath = File(Environment.getExternalStorageDirectory().path + "/RealEstateManager/$id_property")
        if(photoPath.isDirectory){
            val listFiles = photoPath.listFiles()
            if(listFiles != null) {
                for (file in listFiles) {
                    val photo = Photo(file.toUri().toString(), file.nameWithoutExtension, false)
                    listPhoto.add(photo)
                }
            }
        }
        return listPhoto
    }

    fun deletePhotos(idProperty: String, photo: Photo) {
        val root = Environment.getExternalStorageDirectory().path + "/RealEstateManager/"
        val dir = File(root)
        if(!dir.exists()) {
            dir.mkdirs()
        }
        val folder = File(root, "$idProperty/")
        if(!folder.exists()){
            folder.mkdirs()
        }
        val file = File(folder, "${photo.description}.jpg")
        if(file.exists()) file.delete()
    }
}