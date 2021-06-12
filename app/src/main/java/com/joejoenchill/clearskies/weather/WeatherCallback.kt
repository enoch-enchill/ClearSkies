package com.joejoenchill.clearskies.weather

import com.joejoenchill.clearskies.models.WeatherResponse

interface WeatherCallback {
    fun onLoadSuccess(weatherResponse: WeatherResponse?) {}
    fun onLoadFailure(errorMessage: String?) {}
}