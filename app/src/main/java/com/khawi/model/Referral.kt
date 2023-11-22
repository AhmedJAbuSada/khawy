package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Referral(
    @SerialName("shortLink")
    val shortLink: String? = null,
)