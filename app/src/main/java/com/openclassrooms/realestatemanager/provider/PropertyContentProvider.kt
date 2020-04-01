package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.add_edit.addressFromContentValues
import com.openclassrooms.realestatemanager.add_edit.propertyFromContentValues
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import java.lang.IllegalArgumentException


class PropertyContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        val TABLE_PROPERTY = Property::class.java.simpleName
        val TABLE_ADDRESS = Address::class.java.simpleName
        val URI_PROPERTY = Uri.parse("content://$AUTHORITY$TABLE_PROPERTY")
        val URI_ADDRESS = Uri.parse("content://$AUTHORITY$TABLE_ADDRESS")
        const val INT_PROPERTY = 100
        //const val INT_PROPERTY_ID = 101
        const val INT_ADDRESS = 200
        //const val INT_ADDRESS_ID = 201

    }
    private val uriMatcher = buildUriMatcher()

    private fun buildUriMatcher(): UriMatcher{
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(AUTHORITY, TABLE_PROPERTY, INT_PROPERTY)
        //uriMatcher.addURI(AUTHORITY, "$TABLE_PROPERTY/#", INT_PROPERTY_ID)
        uriMatcher.addURI(AUTHORITY, TABLE_ADDRESS, INT_ADDRESS)
        //uriMatcher.addURI(AUTHORITY, "$TABLE_ADDRESS/#", INT_ADDRESS_ID)
        return uriMatcher
    }

    override fun onCreate(): Boolean {
        return true
    }

    @Throws(IllegalArgumentException::class)
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        if(context != null){
            when(uriMatcher.match(uri)){
                INT_PROPERTY -> {
                    val propertyId = ContentUris.parseId(uri)
                    val cursor = PropertyDatabase.getInstance(context!!).propertyDao().getPropertyWithCursor(propertyId.toString())
                    cursor.setNotificationUri(context!!.contentResolver, uri)
                    return cursor
                }
                INT_ADDRESS -> {
                    val addressId = ContentUris.parseId(uri)
                    val cursor = PropertyDatabase.getInstance(context!!).addressDao().getAddressWithCursor(addressId.toString())
                    cursor.setNotificationUri(context!!.contentResolver, uri)
                    return cursor
                }
            }
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String? {
        when(uriMatcher.match(uri)){
            INT_PROPERTY -> return "vnd.android.cursor.property/$AUTHORITY.$TABLE_PROPERTY"
            //INT_PROPERTY_ID -> return "vnd.android.cursor.item/$AUTHORITY.$TABLE_PROPERTY"
            INT_ADDRESS -> return "vnd.android.cursor.address/$AUTHORITY.$TABLE_ADDRESS"
            //INT_ADDRESS_ID -> return "vnd.android.cursor.item/$AUTHORITY.$TABLE_ADDRESS"
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    @Throws(IllegalArgumentException::class)
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if(context != null){
            when(uriMatcher.match(uri)){
                INT_PROPERTY -> {
                    val id = PropertyDatabase.getInstance(context!!).propertyDao().addProperty(propertyFromContentValues(values))
                    if(id != 0L){
                        context!!.contentResolver.notifyChange(uri, null)
                        return ContentUris.withAppendedId(uri, id)
                    }
                }
                INT_ADDRESS -> {
                    val id = PropertyDatabase.getInstance(context!!).addressDao().addAddress(addressFromContentValues(values))
                    if(id != 0L){
                        context!!.contentResolver.notifyChange(uri, null)
                        return ContentUris.withAppendedId(uri, id)
                    }
                }
            }
        }
        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    @Throws(IllegalArgumentException::class)
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        if(context != null){
            when(uriMatcher.match(uri)){
                INT_PROPERTY -> {
                    val count = PropertyDatabase.getInstance(context!!).propertyDao().updatePropertyType(propertyFromContentValues(values).type, propertyFromContentValues(values).id_property)
                    context!!.contentResolver.notifyChange(uri, null)
                    return count
                }
            }
        }
        throw IllegalArgumentException("Failed to update row into $uri")
    }

    @Throws(IllegalArgumentException::class)
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw IllegalArgumentException("Can't delete this property")
    }

}