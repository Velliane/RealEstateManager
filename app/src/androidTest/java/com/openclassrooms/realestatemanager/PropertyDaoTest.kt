package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.database.PropertyDatabase
import com.openclassrooms.realestatemanager.model.Property
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception


@RunWith(AndroidJUnit4::class)
class PropertyDaoTest {

    private lateinit var propertyDatabase: PropertyDatabase
    private val propertyId: Int = 1
    private val property1 = Property(propertyId, "House", 256000, 95, 3, 1, 2, "Little house", true)
    private val property2 = Property(2, "House", 350000, 115, 4, 2, 3, "Big house", true)


    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun initDb(){
        propertyDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context, PropertyDatabase::class.java)
                .allowMainThreadQueries().build()
    }

    @After
    @Throws(Exception::class)
    fun closeDb(){
        propertyDatabase.close()
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertAndGetOneProperty(){
        propertyDatabase.propertyDao().addProperty(property1)

        val property = LiveDataTestUtil.getValue(propertyDatabase.propertyDao().getPropertyFromId(propertyId))

        assertTrue(property.type == property1.type)
        assertTrue(property.price == property1.price)
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertTwoPropertyAndGetAll(){
        propertyDatabase.propertyDao().addProperty(property1)
        propertyDatabase.propertyDao().addProperty(property2)

        val properties = LiveDataTestUtil.getValue(propertyDatabase.propertyDao().getAllProperties())

        assertTrue(properties.size == 2)
    }

    @Test
    @Throws(InterruptedException::class)
    fun getPropertyWhenDatabaseEmpty(){
        val properties = LiveDataTestUtil.getValue(propertyDatabase.propertyDao().getAllProperties())
        assertTrue(properties.isEmpty())
    }

}