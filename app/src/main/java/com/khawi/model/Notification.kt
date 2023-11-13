package com.khawi.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Notification(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("fromId")
    val fromId: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("msg")
    val msg: String? = null,
    @SerialName("dt_date")
    val dtDate: String? = null,
    @SerialName("type")
    val type: Int? = null,
    @SerialName("body_parms")
    val bodyParams: String? = null,
    @SerialName("fromName")
    val fromName: String? = null,
    @SerialName("toName")
    val toName: String? = null,
) : Parcelable