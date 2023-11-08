package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class StaticPage(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("Type")
    val type: String? = null,
    @SerialName("Title")
    val title: String? = null,
    @SerialName("Content")
    val content: String? = null,
)