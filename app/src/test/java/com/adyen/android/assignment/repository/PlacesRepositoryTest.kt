package com.adyen.android.assignment.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adyen.android.assignment.TestCoroutineRule
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.model.GeoCode
import com.adyen.android.assignment.api.model.Main
import com.adyen.android.assignment.api.model.PlacesResponseDto
import com.adyen.android.assignment.api.util.Result
import com.adyen.android.assignment.domain.ddd.Venue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PlacesRepositoryTest {

    private lateinit var placesRepository: PlacesRepository
    private val mockService: PlacesService = Mockito.mock(PlacesService::class.java)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = TestCoroutineRule()

    @BeforeEach
    fun beforeEach() {
        placesRepository = PlacesRepository(mockService)
    }

    @Test
    fun `assert place service should return success result`() = mainCoroutineRule.runBlockingTest {
        val fakeLat = 52.1345
        val fakeLong = 52.7892
        val fakeVenueRecommendationsQueryBuilder: Map<String, String> =
            VenueRecommendationsQueryBuilder().setLatitudeLongitude(
                fakeLat,
                fakeLong
            ).build()
        val mockResponse: Response<PlacesResponseDto> =
            Response.success(Mockito.mock(PlacesResponseDto::class.java))
        Mockito.`when`(mockService.getVenueRecommendations(fakeVenueRecommendationsQueryBuilder))
            .thenReturn(mockResponse)
        val result = mutableListOf<Result<List<Venue>>>()

        placesRepository.fetchVenueRecommendations(fakeLat, fakeLong).toList(result)

        assert(result.last() is Result.Success)
    }

    @Test
    fun `assert place service should return error result`() = mainCoroutineRule.runBlockingTest {
        val fakeLat = 52.1345
        val fakeLong = 52.7892
        val fakeVenueRecommendationsQueryBuilder: Map<String, String> =
            VenueRecommendationsQueryBuilder().setLatitudeLongitude(
                fakeLat,
                fakeLong
            ).build()
        Mockito.`when`(mockService.getVenueRecommendations(fakeVenueRecommendationsQueryBuilder))
            .thenReturn(
                Response.error(
                    503,
                    "".toResponseBody()
                )
            )
        val result = mutableListOf<Result<List<Venue>>>()
        placesRepository.fetchVenueRecommendations(fakeLat, fakeLong).toList(result)
        assert(result.last() is Result.Error)

    }

    @Test
    fun `assert place service should return success with expected venues list`() =
        mainCoroutineRule.runBlockingTest {
            val fakeLat = 52.1345
            val fakeLong = 52.7892
            val fakeName = "Dharmavaram"
            val fakeVenueRecommendationsQueryBuilder: Map<String, String> =
                VenueRecommendationsQueryBuilder().setLatitudeLongitude(
                    fakeLat,
                    fakeLong
                ).build()
            val fakeResults: MutableList<com.adyen.android.assignment.api.model.Result> =
                mutableListOf<com.adyen.android.assignment.api.model.Result>().apply {
                    add(
                        com.adyen.android.assignment.api.model.Result(
                            name = fakeName,
                            geocodes = GeoCode(Main(fakeLat, fakeLong)),
                            categories = null,
                            distance = null,
                            timezone = null,
                            location = null
                        )
                    )
                }
            val fakePlacesResponseDto = PlacesResponseDto(results = fakeResults)
            val fakeResponse: Response<PlacesResponseDto> = Response.success(fakePlacesResponseDto)
            Mockito.`when`(mockService.getVenueRecommendations(fakeVenueRecommendationsQueryBuilder))
                .thenReturn(fakeResponse)
            val result = mutableListOf<Result<List<Venue>>>()

            placesRepository.fetchVenueRecommendations(fakeLat, fakeLong).toList(result)
            println(result.last())

            assert(result.last() is Result.Success)
            val data = (result.last() as Result.Success).data
            assert(data.size == 1)
            assert(data.first().name == fakeName)
        }

}