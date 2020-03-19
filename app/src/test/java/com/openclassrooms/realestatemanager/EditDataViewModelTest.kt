package com.openclassrooms.realestatemanager

import android.content.Context
import com.openclassrooms.realestatemanager.add_edit.EditDataViewModel
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.search.TypeEnum
import com.openclassrooms.realestatemanager.utils.FakePhotoDataRepository
import com.openclassrooms.realestatemanager.utils.FakePropertyDataRepository
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.concurrent.Executor

@RunWith(JUnit4::class)
class EditDataViewModelTest {


    private lateinit var viewModel: EditDataViewModel
    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var fakePhotoDataRepository: FakePhotoDataRepository
    @Mock
    private lateinit var fakePropertyDataRepository: FakePropertyDataRepository
    @Mock
    private lateinit var addressDataRepository: AddressDataRepository
    @Mock
    private lateinit var executor: Executor
    @Mock
    private lateinit var userDataRepository: UserDataRepository

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp(){
        viewModel = EditDataViewModel(context, fakePhotoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, userDataRepository)
    }

    @Test
    fun getTypesEnumListTest(){
        val list = arrayListOf(TypeEnum.HOUSE, TypeEnum.APARTMENT, TypeEnum.DUPLEX, TypeEnum.LOFT)
        val listTest = viewModel.getTypesList()
        assertEquals(list, listTest)
    }
}