package com.adyen.android.assignment.api.util

/**
Since Sealed class provides the advantages of both enum and
abstraction, here created two states for UI and wrapped the required
response init.
 */
sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val errorMessage: String) : Result<Nothing>()

}