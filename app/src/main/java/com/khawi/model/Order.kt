package com.khawi.model

import android.os.Parcelable
import com.khawi.model.db.user.UserModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Order(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("days")
    val days: MutableList<String>? = null,
    @SerialName("title")
    val title: String? = null,
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
    @SerialName("max_price")
    val maxPrice: String? = null,
    @SerialName("min_price")
    val minPrice: String? = null,
    @SerialName("price")
    val price: String? = null,
    @SerialName("is_repeated")
    val isRepeated: Boolean? = null,
    @SerialName("orderType")
    val orderType: Int? = null,
    @SerialName("max_passenger")
    val maxPassenger: Int? = null,
    @SerialName("notes")
    val notes: String? = null,
    @SerialName("canceled_note")
    val canceledNote: String? = null,
    @SerialName("order_no")
    val orderNo: String? = null,
    @SerialName("tax")
    val tax: String? = null,
    @SerialName("totalDiscount")
    val totalDiscount: String? = null,
    @SerialName("netTotal")
    val netTotal: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("createAt")
    val createAt: String? = null,
    @SerialName("user")
    val user: UserModel? = null,
    @SerialName("loc")
    val loc: Loc? = null,
    @SerialName("passengers")
    val passengers: MutableList<String>? = null,
    @SerialName("offers")
    val offers: MutableList<Offer>? = null,
) : Parcelable