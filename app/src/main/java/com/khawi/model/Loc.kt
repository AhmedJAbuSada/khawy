package com.khawi.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Loc(
    @SerialName("type")
    val type: String? = null,
    @SerialName("coordinates")
    val coordinates: MutableList<String>? = null,
) : Parcelable