package com.joejoenchill.clearskies.models

import java.io.Serializable
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("timezone_offset") val timezone_offset: Long,
    @SerializedName("daily") val daily: List<DailyItem>,
): Serializable