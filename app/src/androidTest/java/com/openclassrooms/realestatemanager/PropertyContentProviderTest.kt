package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.provider.PropertyContentProvider
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PropertyContentProviderTest {

    private lateinit var contentResolver: ContentResolver
    private val PROPERTY_ID = 1L

    @Before
    fun setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context, PropertyDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    fun getPropertiesWhenNoPropertyInserted() {
        val cursor = contentResolver.query(ContentUris.withAppendedId(PropertyContentProvider.URI_PROPERTY, PROPERTY_ID), null, null, null, null)
        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(0))
        cursor?.close()
    }

    @Test
    fun insertAndGetItem() {
        val propertyUri: Uri? = contentResolver.insert(PropertyContentProvider.URI_PROPERTY, generateItem())
        val cursor: Cursor? = contentResolver.query(ContentUris.withAppendedId(PropertyContentProvider.URI_PROPERTY, PROPERTY_ID), null, null, null, null)
        assertThat(cursor, notNullValue())
        assertThat(cursor!!.count, `is`(1))
        assertThat(cursor.moveToFirst(), `is`(true))
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("description")), `is`("Superbe maison, à visiter d'urgence !"))
    }

    private fun generateItem(): ContentValues? {
        val values = ContentValues()
        values.put("property_id", "1")
        values.put("agent", "5")
        values.put("type", "HOUSE")
        values.put("price", 242000)
        values.put("surface", 96)
        values.put("rooms_nbr", 4)
        values.put("bath_nbr", 1)
        values.put("bed_nbr", 3)
        values.put("description", "Superbe maison, à visiter d'urgence !")
        values.put("in_sale", true)
        values.put("nearby", "BUS, SCHOOL")
        values.put("created_date", "20/03/2020")
        values.put("sold_date", "")
        values.put("date", "2020-03-23T12:20:25")
        return values
    }
}