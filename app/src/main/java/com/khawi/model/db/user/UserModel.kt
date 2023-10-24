package com.khawi.model.db.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "user", primaryKeys = ["id"])
data class UserModel(
    @SerialName("_id")
    @ColumnInfo("id")
    val id: String = "",
    @SerialName("address")
    @ColumnInfo("address")
    val address: String? = "",
    @SerialName("createAt")
    @ColumnInfo("createAt")
    val createAt: String? = "",
    @SerialName("isVerify")
    @ColumnInfo("isVerify")
    val isVerify: Boolean? = true,
    @SerialName("wallet")
    @ColumnInfo("wallet")
    val wallet: Double? = 0.0,
    @SerialName("isEnableNotifications")
    @ColumnInfo("isEnableNotifications")
    val isEnableNotifications: Boolean? = true,
    @SerialName("level")
    @ColumnInfo("level")
    val level: String? = "",
    @SerialName("type")
    @ColumnInfo("type")
    val type: String? = "",
    @SerialName("arName")
    @ColumnInfo("arName")
    val arName: String? = "",
    @SerialName("enName")
    @ColumnInfo("enName")
    val enName: String? = "",
    @ColumnInfo("name")
    val name: String? = "",
    @SerialName("email")
    @ColumnInfo("email")
    val email: String? = "",
    @SerialName("phone_number")
    @ColumnInfo("phone_number")
    val phoneNumber: String? = "",
    @SerialName("gender")
    @ColumnInfo("gender")
    val gender: String? = "",
    @SerialName("token")
    @ColumnInfo("token")
    val token: String? = "",
    @SerialName("image")
    @ColumnInfo("image")
    val image: String? = "",
    @SerialName("lat")
    @ColumnInfo("lat")
    val lat: Double? = 0.0,
    @SerialName("lng")
    @ColumnInfo("lng")
    val lng: Double? = 0.0,
    @SerialName("isDeleted")
    @ColumnInfo("isDeleted")
    val isDeleted: Boolean? = false,
    @SerialName("fcmToken")
    @ColumnInfo("fcmToken")
    val fcmToken: String? = "",
)