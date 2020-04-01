package com.openclassrooms.realestatemanager

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.openclassrooms.realestatemanager.utils.Utils
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class UtilsTest {

    @Mock
    private lateinit var context: Context

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

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

    @Test
    fun whenInternetIsNotAvailable() {
        val connManager = Mockito.mock(ConnectivityManager::class.java)
        val networkInfo = Mockito.mock(NetworkInfo::class.java)
        val packageManager = Mockito.mock(PackageManager::class.java)

        Mockito.`when`(context.packageManager).thenReturn(packageManager)
        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connManager)
        Mockito.`when`(connManager.activeNetworkInfo).thenReturn(networkInfo)
        Mockito.`when`(networkInfo.isConnected).thenReturn(false)

        assertFalse(Utils.isInternetAvailable(context))
    }

    @Test
    fun whenInternetIsAvailable() {
        val connManager = Mockito.mock(ConnectivityManager::class.java)
        val networkInfo = Mockito.mock(NetworkInfo::class.java)
        val packageManager = Mockito.mock(PackageManager::class.java)

        Mockito.`when`(context.packageManager).thenReturn(packageManager)
        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connManager)
        Mockito.`when`(connManager.activeNetworkInfo).thenReturn(networkInfo)
        Mockito.`when`(networkInfo.isConnected).thenReturn(true)

        assertTrue(Utils.isInternetAvailable(context))
    }
}