package com.joejoenchill.clearskies.models

import com.google.gson.annotations.SerializedName

data class FeelsLikeItem(
    @SerializedName("day") val day: Double,
    @SerializedName("night") val night: Double,
    @SerializedName("eve") val eve: Double,
    @SerializedName("morn") val morn: Double
)
