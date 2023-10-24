package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Welcome(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("description")
    val description: String? = null,
    var v: Long? = null
)