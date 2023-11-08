package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ContactUsBody(
    @SerialName("details")
    val details: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
)