package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserBody(
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("os")
    val os: String? = null,
    @SerialName("fcmToken")
    val fcmToken: String? = null,
    @SerialName("lat")
    val lat: String? = null,
    @SerialName("lng")
    val lng: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("_id")
    val _id: String? = null,
    @SerialName("verify_code")
    val verifyCode: String? = null,
)