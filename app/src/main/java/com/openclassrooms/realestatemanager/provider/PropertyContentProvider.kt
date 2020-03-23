package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.add_edit.fromContentValues
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import java.lang.IllegalArgumentException


class PropertyContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        val TABLE_PROPERTY = Property::class.java.simpleName
        val URI_PROPERTY = Uri.parse("content://$AUTHORITY$TABLE_PROPERTY")
    }




    override fun onCreate(): Boolean {
        return true
    }

    @Throws(IllegalArgumentException::class)
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        if(context != null){
            val propertyId = ContentUris.parseId(uri)
            val cursor = PropertyDatabase.getInstance(context!!).propertyDao().getPropertyWithCursor(propertyId.toString())
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.propery/$AUTHORITY.$TABLE_PROPERTY"
    }

    @Throws(IllegalArgumentException::class)
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if(context != null){
            val id = PropertyDatabase.getInstance(context!!).propertyDao().addProperty(fromContentValues(values))
            if(id != 0L){
                context!!.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
        }
        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    @Throws(IllegalArgumentException::class)
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        if(context != null){
            val count = PropertyDatabase.getInstance(context!!).propertyDao().updatePropertyType(fromContentValues(values).type, fromContentValues(values).id_property)
            context!!.contentResolver.notifyChange(uri, null)
            return count
        }
        throw IllegalArgumentException("Failed to update row into $uri")
    }

    @Throws(IllegalArgumentException::class)
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw IllegalArgumentException("Can't delete this property")
    }

}