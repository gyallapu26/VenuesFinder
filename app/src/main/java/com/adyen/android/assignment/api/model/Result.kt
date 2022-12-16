package com.adyen.android.assignment.api.model

/** API returning null values sometimes, to solve this issue marking entities nullable */
data class Result(
    val categories: List<Category>?,
    val distance: Int?,
    /*Api is not returning geocode, it's geocodes*/
    val geocodes: GeoCode?,
    val location: Location?,
    val name: String?,
    val timezone: String?,
)