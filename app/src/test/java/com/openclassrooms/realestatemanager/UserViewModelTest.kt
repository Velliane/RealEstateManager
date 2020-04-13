package com.openclassrooms.realestatemanager

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.FirebaseApp
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.login.UserViewModel
import com.openclassrooms.realestatemanager.utils.TestHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class UserViewModelTest {

    private lateinit var userViewModel: UserViewModel
    @Mock
    private lateinit var context: Context


    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = runBlockingTest {
        MockitoAnnotations.initMocks(this)
        TestHelper.initialize()
    }

    @Test
    fun saveUserTest() = runBlockingTest {
        val sharedPreferences = Mockito.mock(SharedPreferences::class.java)
        val context = Mockito.mock(Context::class.java)
        Mockito.`when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        val user = User("566413", "Martin", "martin@orange.fr", "profile.martin/image.fr")
        val success = 1L
        val mockUserDataRepository = mock<UserDataRepository> {
            onBlocking { addUser(user) } doReturn success
        }

        userViewModel = UserViewModel(mockUserDataRepository)
        userViewModel.saveUser(user, context, sharedPreferences)
    }

}