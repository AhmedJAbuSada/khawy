package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Coupon(
    @SerialName("final_total")
    val finalTotal: Double? = null,
    @SerialName("discount")
    val discount: Double? = null,
)