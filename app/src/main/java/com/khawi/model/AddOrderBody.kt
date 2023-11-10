package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AddOrderBody(
    @SerialName("couponCode")
    val couponCode: String = "",
    @SerialName("paymentType")
    val paymentType: Int? = null,
    @SerialName("dt_date")
    val dtDate: String? = null,
    @SerialName("dt_time")
    val dtTime: String? = null,
    @SerialName("f_address")
    val fAddress: String? = null,
    @SerialName("t_address")
    val tAddress: String? = null,
    @SerialName("f_lat")
    val fLat: Double? = null,
    @SerialName("f_lng")
    val fLng: Double? = null,
    @SerialName("t_lat")
    val tLat: Double? = null,
    @SerialName("t_lng")
    val tLng: Double? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("max_price")
    val maxPrice: String? = null,
    @SerialName("min_price")
    val minPrice: String? = null,
    @SerialName("price")
    val price: String? = null,
    @SerialName("is_repeated")
    val isRepeated: Boolean? = null,
    @SerialName("days")
    val days: String? = null,
    @SerialName("orderType")
    val orderType: Int? = null,
    @SerialName("max_passenger")
    val maxPassenger: Int? = null,
    @SerialName("notes")
    val notes: String? = null,
)