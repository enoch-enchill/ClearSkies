package com.joejoenchill.clearskies.models


import com.google.gson.annotations.SerializedName

data class DailyItem(
    @SerializedName("dt")
    val dt: Long,

    @SerializedName("sunrise")
    val sunrise: Long,

    @SerializedName("sunset")
    val sunset: Long,

    @SerializedName("temp")
    val temp: TempItem,

    @SerializedName("feels_like")
    val feelsLike: FeelsLikeItem,

    @SerializedName("pressure")
    val pressure: Int,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("dew_point")
    val dewPoint: Double,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("wind_deg")
    val windDeg: Int,

    @SerializedName("weather")
    val weather: List<WeatherItem>,

    @SerializedName("clouds")
    val clouds: Int,

    @SerializedName("pop")
    val pop: Double,

    @SerializedName("uvi")
    val uvi: Double
)
