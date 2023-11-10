package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ChangeStatusBody(
    @SerialName("offer")
    val offer: String = "",
    @SerialName("status")
    val status: String? = null,
    @SerialName("canceled_note")
    val canceledNote: String? = null,
)