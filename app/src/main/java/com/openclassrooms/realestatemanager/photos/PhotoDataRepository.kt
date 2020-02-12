package com.openclassrooms.realestatemanager.photos

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PhotoDataRepository {

    fun saveImageToExternalStorage(bitmap: Bitmap, id_property: String, description: String, context:Context): Uri {

        val file = File(context.getExternalFilesDir(null), "$id_property$description.jpg")
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
        val filename = "$id_property$description"
        val storageReference = FirebaseStorage.getInstance().getReference("/images/$filename")
        storageReference.putFile(uri).addOnSuccessListener {
            Log.d("PHOTO", "Photo successfully saved in Firebase")
        }
        // child("$id_property/$description.jpg"
    }
}