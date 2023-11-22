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
    @SerialName("address")
    val address: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("_id")
    val _id: String? = null,
    @SerialName("verify_code")
    val verifyCode: String? = null,
    @SerialName("hasCar")
    val hasCar: Boolean? = null,
    @SerialName("carType")
    val carType: String? = null,
    @SerialName("carModel")
    val carModel: String? = null,
    @SerialName("carColor")
    val carColor: String? = null,
    @SerialName("carNumber")
    val carNumber: String? = null,
    @SerialName("by")
    val by: String? = null,
)