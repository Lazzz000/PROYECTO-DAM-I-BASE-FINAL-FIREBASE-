package com.nexushardware.app.utils.resources

sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
    class Idle<T> : Resource<T>()
}