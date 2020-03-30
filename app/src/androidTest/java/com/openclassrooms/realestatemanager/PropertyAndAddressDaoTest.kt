package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class PropertyAndAddressDaoTest {

    private lateinit var propertyDatabase: PropertyDatabase
    private val propertyId: String = "1"
    private val property1 = Property(propertyId, "025","House", 256000, 95, 3, 1, 2, "Little house", true, "")
    private val property2 = Property("2", "025","House", 350000, 115, 4, 2, 3, "Big house", true, "")
    private val addressTest = Address("1", 4, "allée des Bleuets", "71500", "Louhans", "France", propertyId)
    private val addresTest2 = Address("2", 22, "rue des Lilas", "01350", "Culoz", "France", "2")

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
    fun insertAndGetOneProperty() = runBlockingTest{
        propertyDatabase.propertyDao().addProperty(property1)

        val property = propertyDatabase.propertyDao().getPropertyFromId(propertyId)

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
        assertEquals(2, properties[0].bed_nbr)
        assertEquals(2, properties[1].bath_nbr)
    }

    @Test
    @Throws(InterruptedException::class)
    fun getPropertyWhenDatabaseEmpty(){
        val properties = LiveDataTestUtil.getValue(propertyDatabase.propertyDao().getAllProperties())
        assertTrue(properties.isEmpty())
    }


    @Test
    @Throws(InterruptedException::class)
    fun insertTwoAddressAndGetAll(){
        propertyDatabase.propertyDao().addProperty(property1)
        propertyDatabase.addressDao().addAddress(addressTest)

        propertyDatabase.propertyDao().addProperty(property2)
        propertyDatabase.addressDao().addAddress(addresTest2)

        val addresses = LiveDataTestUtil.getValue(propertyDatabase.addressDao().getAllAddress())

        assertTrue(addresses.size == 2)
    }

    @Test
    @Throws(InterruptedException::class)
    fun getAddressOfOneProperty(){
        propertyDatabase.propertyDao().addProperty(property1)
        propertyDatabase.addressDao().addAddress(addressTest)

        val address = propertyDatabase.addressDao().getAddressOfOneProperty(propertyId)

        assertTrue(address.city == addressTest.city)
        assertTrue(address.street == addressTest.street)
    }

}