package com.adyen.android.assignment.repository

import android.util.Log
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.model.PlacesResponseDto
import com.adyen.android.assignment.api.util.Result
import com.adyen.android.assignment.domain.ddd.Venue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlacesRepository @Inject constructor(private val placesService: PlacesService) :
    IPlacesRepository {

    override suspend fun fetchVenueRecommendations(
        latitude: Double,
        longitude: Double
    ): Flow<Result<List<Venue>>> = flow {
        try {
            val response =
                placesService.getVenueRecommendations(
                    VenueRecommendationsQueryBuilder().setLatitudeLongitude(
                        latitude,
                        longitude
                    ).build()
                )
            if (response.isSuccessful) {
                emit(Result.Success(response.body().toDomainEntity()))
            } else {
                emit(Result.Error((response.errorBody().toString())))
            }
        } catch (exception: Exception) {
            emit(Result.Error(exception.localizedMessage.orEmpty()))
        }
    }
}

private fun PlacesResponseDto?.toDomainEntity(): List<Venue> {
    if (this == null || this.results.isNullOrEmpty()) return emptyList()
    val venues: MutableList<Venue> = mutableListOf()
    this.results.forEach {
        venues += it.toVenue()
    }
    return venues
}

private fun com.adyen.android.assignment.api.model.Result.toVenue(): Venue {
    /** API returning null values sometimes, to solve this issue marking nullable entities */
    return Venue(
        coordinates = LatLng(
            this.geocodes?.main?.latitude ?: 0.0,
            this.geocodes?.main?.longitude ?: 0.0
        ),
        name = this.name.orEmpty(),
        address = this.location?.formatted_address.orEmpty()
    )
}

