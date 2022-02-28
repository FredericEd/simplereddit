package com.fveloz.redditreader.utilities

sealed class ResultState<T> {
    data class Success<T>(val data: T): ResultState<T>()
    data class Error<T>(val message: String): ResultState<T>()
    class Empty<T>: ResultState<T>()
}