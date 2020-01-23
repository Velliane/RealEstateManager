package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.espresso.Root
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
    private val propertyTest = Property(propertyId, "House", 256000, 95, 3, 1, 2, "Little house", true)

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
    fun insertProperty(){
        propertyDatabase.propertyDao().addProperty(propertyTest)

        val property = LiveDataTestUtil.getValue(propertyDatabase.propertyDao().getPropertyFromId(propertyId))

        assertTrue(property.type == propertyTest.type)
        assertTrue(property.price == propertyTest.price)
    }
}