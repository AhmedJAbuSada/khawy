package com.khawi.model.db.user

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(tableName = "user", primaryKeys = ["id"])
data class UserModel(
    @SerialName("_id")
    @ColumnInfo("id")
    val id: String = "",
    @ColumnInfo("isVerify")
    val isVerify: Boolean? = true,
    @ColumnInfo("address")
    val address: String? = "",
    @ColumnInfo("createAt")
    val createAt: String? = "",
    @ColumnInfo("wallet")
    val wallet: Double? = 0.0,
    @SerialName("full_name")
    @ColumnInfo("full_name")
    val fullName: String? = "",
    @ColumnInfo("email")
    val email: String? = "",
    @SerialName("phone_number")
    @ColumnInfo("phone_number")
    val phoneNumber: String? = "",
    @ColumnInfo("lat")
    val lat: Double? = 0.0,
    @ColumnInfo("lng")
    val lng: Double? = 0.0,
    @ColumnInfo("token")
    val token: String? = "",
    @ColumnInfo("isEnableNotifications")
    val isEnableNotifications: Boolean? = true,
    @ColumnInfo("image")
    val image: String? = "",
    @ColumnInfo("carColor")
    val carColor: String? = "",
    @ColumnInfo("carModel")
    val carModel: String? = "",
    @ColumnInfo("carNumber")
    val carNumber: String? = "",
    @ColumnInfo("carType")
    val carType: String? = "",
    @ColumnInfo("hasCar")
    val hasCar: Boolean? = false,
    @ColumnInfo("rate")
    val rate: String? = "",
    @ColumnInfo("orders")
    val orders: String? = "",
): Parcelable