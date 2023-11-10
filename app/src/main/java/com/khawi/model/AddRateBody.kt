package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AddRateBody(
    @SerialName("rate_from_user")
    val rateFromUser: String = "",
    @SerialName("note_from_user")
    val noteFromUser: String? = null,
)