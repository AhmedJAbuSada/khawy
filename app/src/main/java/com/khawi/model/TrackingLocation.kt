package com.khawi.model

data class TrackingLocation(
    val lastUpdate: Long? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val orderId: String? = null,
    val status: String? = null,
    val userId: String? = null,
)