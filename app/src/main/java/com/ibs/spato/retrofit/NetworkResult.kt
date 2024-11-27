package com.ibs.spato.retrofit

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {
    class Loading<T> : NetworkResult<T>()
    class Success<T>(data: T? = null) : NetworkResult<T>(data)
    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)
    class NoInternet<T>(message: String?) : NetworkResult<T>(message = message)
}