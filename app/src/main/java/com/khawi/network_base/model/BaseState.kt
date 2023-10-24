package com.khawi.network_base.model


sealed class BaseState<T> {
    class Idle<T>() : BaseState<T>()
    class NetworkError<T>() : BaseState<T>()
    class EmptyResult<T>() : BaseState<T>()
    data class ItemsLoaded<T>(val items: T) : BaseState<T>()
}