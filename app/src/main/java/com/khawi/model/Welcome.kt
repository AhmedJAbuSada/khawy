package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Welcome(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("Title")
    val name: String? = null,
    @SerialName("Description")
    val description: String? = null,
    var v: Long? = null
)