package com.joejoenchill.clearskies.data

import com.joejoenchill.clearskies.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("onecall")
    fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unitName: String,
        @Query("exclude") exclude: String,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
}