package com.adyen.android.assignment.api.model

data class Location(
    val formatted_address: String,
    val address: String,
    val country: String,
    val locality: String,
    val neighbourhood: List<String>,
    val postcode: String,
    val region: String,
)
