package com.khawi.model

import android.os.Parcelable
import com.khawi.model.db.user.UserModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Offer(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("user")
    val user: UserModel? = null,
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
    @SerialName("price")
    val price: String? = null,
    @SerialName("notes")
    val notes: String? = null,
    @SerialName("status")
    val status: String? = null,
) : Parcelable