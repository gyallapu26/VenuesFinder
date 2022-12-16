package com.adyen.android.assignment.api.model

/** API returning null values sometimes, to solve this issue marking entities nullable */
data class Category(
    val icon: Icon?,
    val id: String?,
    val name: String?,
)
