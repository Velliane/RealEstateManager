package com.openclassrooms.realestatemanager.photos

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PhotoDataRepository {

    fun saveImageToExternalStorage(bitmap: Bitmap, id_property: String, description: String, context:Context): Uri {

        //val file = File(context.getExternalFilesDir(null), "$id_property$description.jpg")
        val root = Environment.getExternalStorageDirectory().path + "/RealEstateManager/$id_property"
        val dir = File(root)
        dir.mkdirs()

        val file = File(root, "$description.jpg")
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
        return Uri.parse(file.absolutePath)
    }

    fun saveImageToFirebase(uri: Uri, id_property: String, description: String){
        val storageReference = FirebaseStorage.getInstance().getReference("/images/$id_property/$description")
        storageReference.putFile(uri).addOnSuccessListener {
            Log.d("PHOTO", "Photo successfully saved in Firebase")
        }
    }

    fun getListOfPhotos(id_property: String): List<Photo>{
        val listPhoto = ArrayList<Photo>()
        val photoPath = File(Environment.getExternalStorageDirectory().path + "/RealEstateManager/$id_property")
        if(photoPath.isDirectory){
            val listFiles = photoPath.listFiles()
            for(file in listFiles){
                val photo = Photo(file.toUri(), file.name)
                listPhoto.add(photo)
            }
        }
        return listPhoto
    }
}