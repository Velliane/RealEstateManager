package com.openclassrooms.realestatemanager.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun saveListOfPhotos(list: List<String>?): String?{
        return Gson().toJson(list)
    }

    @TypeConverter
    fun restoreList(value:String?):List<String>? {
        return Gson().fromJson(value, object:TypeToken<List<String>>() {}.type)
    }
}