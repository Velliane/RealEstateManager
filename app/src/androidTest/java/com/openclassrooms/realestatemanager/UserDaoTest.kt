package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.runner.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.login.User
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class UserDaoTest {

    private lateinit var propertyDatabase: PropertyDatabase
    private val USER_ID: String = "1"
    private val USER2_ID: String = "2"
    private val userTest = User(USER_ID, "Eric", "eric.martin@orange.fr", "http://5454544455")
    private val userTest2 = User(USER2_ID, "Antoine", "antoine.roche@free.fr", "http://5674457766")

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
    fun insertUser(){
        propertyDatabase.userDao().addUser(userTest)

        val user = LiveDataTestUtil.getValue(propertyDatabase.userDao().getUserById(USER_ID))

        assertTrue(user.name == userTest.name)
    }

    @Test
    @Throws(InterruptedException::class)
    fun getAllUsers() = runBlockingTest{
        propertyDatabase.userDao().addUser(userTest)
        propertyDatabase.userDao().addUser(userTest2)

        val listOfUser = propertyDatabase.userDao().getAllUsers()
        val listTest = arrayListOf(userTest, userTest2)

        assertEquals(listTest, listOfUser)
    }
}