package com.adyen.android.assignment.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adyen.android.assignment.TestCoroutineRule
import com.adyen.android.assignment.api.util.Result
import com.adyen.android.assignment.domain.ddd.Venue
import com.adyen.android.assignment.domain.usecases.GetPlacesUseCase
import com.adyen.android.assignment.ui.state.VenueUiState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.internal.wait
import org.junit.Rule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`


@OptIn(ExperimentalCoroutinesApi::class)
class PlacesViewModelTest {
    private lateinit var viewModel: PlacesViewModel
    private val mockUseCase: GetPlacesUseCase =
        Mockito.mock(GetPlacesUseCase::class.java)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = TestCoroutineRule()


    @BeforeEach
    fun setUp() {
        viewModel =
            PlacesViewModel(
                mockUseCase,
                mainCoroutineRule.testCoroutineDispatcher
            )
    }

    @Test
    fun `assert getPlacesUseCase should return error result`() = mainCoroutineRule.runBlockingTest {

        val fakeErrorMessage = "Something went wrong!"
        val fakeLat = 52.1345
        val fakeLong = 52.7892
        `when`(mockUseCase(fakeLat, fakeLong))
            .thenReturn(flow { Result.Error(fakeErrorMessage) })
        val result = mutableListOf<VenueUiState>()
        val job = launch {
            viewModel.venueUiState.toList(result)
        }
        viewModel.fetchVenues(fakeLat, fakeLong)
        val currentUiState = result.last()
        assert(currentUiState.venues.isEmpty())
        job.cancel()
    }
}