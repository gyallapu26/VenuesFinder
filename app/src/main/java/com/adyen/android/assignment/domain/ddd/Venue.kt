package com.adyen.android.assignment.domain.ddd

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Venue(
    override val coordinates:  @RawValue LatLng,
    override val name: String,
    override val address: String
) : IVenue, Parcelable