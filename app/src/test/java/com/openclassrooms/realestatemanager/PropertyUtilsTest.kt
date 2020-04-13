package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.search.NearbyEnum
import com.openclassrooms.realestatemanager.utils.compareByDate
import com.openclassrooms.realestatemanager.utils.getNearbyList
import com.openclassrooms.realestatemanager.utils.setAddressToString
import junit.framework.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class PropertyUtilsTest {

    private val property1 = Property("1", "025","House", 0,250000, 85, 3, 1, 2, "Little house", true,"" , "","", "2020-01-12T12:20:25")
    private val property2 = Property("5", "025","House", 0,360000, 105, 4, 1, 3, "Big house", true,"" , "", "", "2020-01-12T12:35:25")


    @Test
    fun compareByDate_ReturnMoreRecent(){
        assertEquals(property2, compareByDate(property1, property2))
    }

    @Test
    fun compareByDate_ReturnOlder() {
        assertEquals(property2, compareByDate(property2, property1))
    }

    @Test
    fun setAddressToString() {
        val address = Address("2", 22, "rue des Lilas", "01350", "Culoz", "France", "002")
        assertEquals("22 rue des Lilas\n01350 Culoz\nFrance", setAddressToString(address))

        val address2 = Address("4", null, null, "02270", "Coligny", "France", "045")
        assertEquals("02270 Coligny\nFrance", setAddressToString(address2))

        val address3 = Address("6", 5, "route de Bourgogne", null, "Coligny", "France", "004")
        assertEquals("5 route de Bourgogne\nColigny\nFrance", setAddressToString(address3))

        val address4 = Address("3", null, "rue des Eglantines", "01350", "Culoz", "France", "003")
        assertEquals("rue des Eglantines\n01350 Culoz\nFrance", setAddressToString(address4))
    }

    @Test
    fun getAllNearbyPlacesFromEnumConstant() {
        val list = listOf(NearbyEnum.BUS, NearbyEnum.SHOP, NearbyEnum.SCHOOL, NearbyEnum.POST, NearbyEnum.PUBLIC_TRANSPORT, NearbyEnum.RESTAURANT, NearbyEnum.UNIVERSITY)
        Assert.assertEquals(list, getNearbyList())
    }
}