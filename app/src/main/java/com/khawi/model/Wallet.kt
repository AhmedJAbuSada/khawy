package com.khawi.model

import com.khawi.model.db.user.UserModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wallet(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("order_no")
    val orderNo: String? = null,
    @SerialName("details")
    val details: String? = null,
    @SerialName("total")
    val total: Double? = null,
    @SerialName("paymentType")
    val paymentType: String? = null,
    @SerialName("createAt")
    val createAt: String? = null,
    @SerialName("user")
    val user: UserModel? = null,
)