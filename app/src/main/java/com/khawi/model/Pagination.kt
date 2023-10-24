package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Pagination(
    @SerialName("totalPages")
    val totalPages: Int? = null,
)