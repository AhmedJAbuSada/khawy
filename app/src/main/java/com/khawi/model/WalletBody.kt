package com.khawi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class WalletBody(
    @SerialName("amount")
    val amount: String? = null,
)