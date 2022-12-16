package com.adyen.android.assignment.ui.state

import com.adyen.android.assignment.domain.ddd.Venue

data class VenueUiState(
    val venues: List<Venue> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String = ""
)