package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BaseResponse<T>(
    @SerialName("status")
    var status: Boolean? = null,
    @SerialName("code")
    val code: Int? = null,
    @SerialName("message")
    var message: String? = null,
    @SerialName("total")
    var total: String? = null,
    @SerialName("last_date")
    var lastDate: String? = null,
    @SerialName("items")
    var data: T? = null,
    @SerialName("showDialog")
    val showDialog: Boolean? = null,
    @SerialName("pagination")
    val pagination: Pagination? = null,
    var v : Long? = null,
)