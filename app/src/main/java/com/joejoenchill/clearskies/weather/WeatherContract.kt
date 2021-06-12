package com.joejoenchill.clearskies.weather

import com.joejoenchill.clearskies.models.WeatherResponse

class WeatherContract {
    interface Model {
        fun loadWeatherData(lat:Double, lon:Double, callback: WeatherCallback)
    }

    interface View {
        fun onLoadFinished(weatherResponse: WeatherResponse?)
        fun onLoadFailed(error: String)
    }

    interface Presenter {
        fun startLoadingData(lat:Double, lon:Double)
    }
}