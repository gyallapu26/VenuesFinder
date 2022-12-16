package com.adyen.android.assignment.domain.usecases

import com.adyen.android.assignment.api.util.Result
import com.adyen.android.assignment.domain.ddd.Venue
import com.adyen.android.assignment.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase encapsulate complex business logic and can be reused by mutiple viewmodels
 * Although @see GetPlacesUseCase might not add much benefit in simple case but
 * when app grows complex it's useful!
 * */
class GetPlacesUseCase @Inject constructor(private val placesRepository: PlacesRepository) :
    IGetPlacesUseCase {

    override suspend fun invoke(latitude: Double, longitude: Double): Flow<Result<List<Venue>>> {
        return placesRepository.fetchVenueRecommendations(latitude, longitude)
    }

}