package com.adyen.android.assignment.domain.ddd

import com.google.android.gms.maps.model.LatLng

/** Following Domain drivern design principle to convert dto's to the domain entities */
interface IVenue {

    /** Coordinates of the Venue */
    val coordinates: LatLng

    /** Name of the Venue */

    val name: String

    /** Formatted address of the Venue */
    val address: String
}