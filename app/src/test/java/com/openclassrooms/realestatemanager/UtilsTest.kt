package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.Utils
import junit.framework.Assert.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun testConvertDollarToEuro() {
        val priceInDollar = 250000
        val priceInDollar2 = 345050
        assertEquals(225000, Utils.convertDollarToEuro(priceInDollar))
        assertEquals(310545, Utils.convertDollarToEuro(priceInDollar2))
    }

    @Test
    fun testConvertEuroToDollar() {
        val priceInEuro = 250000
        val priceInEuro2 = 352022
        assertEquals(280000, Utils.convertEuroToDollar(priceInEuro))
        assertEquals(394265, Utils.convertEuroToDollar(priceInEuro2))
    }
}