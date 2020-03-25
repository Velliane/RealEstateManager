package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.simulator.SimulatorViewModel
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.math.BigDecimal
import java.math.RoundingMode


@RunWith(JUnit4::class)
class SimulatorViewModelTest {

    private lateinit var viewModel: SimulatorViewModel

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        viewModel = SimulatorViewModel()
    }

    @Test
    fun calculateMonthlyWith_250000Capital_15YearDuration_2Point2PerCentRate(){
        viewModel.calculateMonthlyPayment(250000, 15, 2.2f)
        val result = BigDecimal(1634.39).setScale(2, RoundingMode.HALF_UP)
        assertEquals(result, viewModel.monthlyLiveData.value)
    }

    @Test
    fun calculateMonthlyWith_350000Capital_25YearDuration_1Point8PerCentRate(){
        viewModel.calculateMonthlyPayment(350000, 28, 1.8f)
        val result = BigDecimal(1326.89).setScale(2, RoundingMode.HALF_UP)
        assertEquals(result, viewModel.monthlyLiveData.value)
    }

}