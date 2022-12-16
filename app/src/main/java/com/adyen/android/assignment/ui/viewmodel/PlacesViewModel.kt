package com.adyen.android.assignment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.api.util.Result
import com.adyen.android.assignment.domain.usecases.GetPlacesUseCase
import com.adyen.android.assignment.ui.state.VenueUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val placesUseCase: GetPlacesUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _venuesUiState: MutableStateFlow<VenueUiState> =
        MutableStateFlow(VenueUiState())
    val venueUiState = _venuesUiState.asStateFlow()


     fun fetchVenues(latitude: Double, longitude: Double) {
        viewModelScope.launch(ioDispatcher) {
            placesUseCase(latitude, longitude).collectLatest { result ->
                _venuesUiState.update { currentState ->
                    when (result) {
                        is Result.Success -> {
                            currentState.copy(
                                isLoading = false,
                                errorMessage = "",
                                venues = result.data
                            )
                        }
                        is Result.Error -> {
                            currentState.copy(
                                isLoading = false,
                                errorMessage = result.errorMessage,
                                venues = emptyList()
                            )
                        }
                    }
                }
            }
        }
    }
}