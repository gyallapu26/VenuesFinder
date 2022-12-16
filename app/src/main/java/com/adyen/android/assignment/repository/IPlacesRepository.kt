package com.adyen.android.assignment.repository

import com.adyen.android.assignment.api.util.Result
import com.adyen.android.assignment.domain.ddd.Venue
import kotlinx.coroutines.flow.Flow

interface IPlacesRepository {

    suspend fun fetchVenueRecommendations(latitude: Double, longitude: Double): Flow<Result<List<Venue>>>
}