package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Day(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    var select: Boolean = false,
    var v: Long? = null
)