package com.adyen.android.assignment.domain.usecases

import com.adyen.android.assignment.api.model.PlacesResponseDto
import com.adyen.android.assignment.api.util.Result
import com.adyen.android.assignment.domain.ddd.Venue
import kotlinx.coroutines.flow.Flow

interface IGetPlacesUseCase {

    suspend operator fun invoke(latitude: Double, longitude: Double): Flow<Result<List<Venue>>>
}
