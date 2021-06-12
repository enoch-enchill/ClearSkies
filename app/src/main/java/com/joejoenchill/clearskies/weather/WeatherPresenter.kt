package com.joejoenchill.clearskies.weather

import com.joejoenchill.clearskies.models.WeatherResponse

class WeatherPresenter(view: WeatherContract.View) : WeatherContract.Presenter {

    private var view: WeatherContract.View
    private var model: WeatherContract.Model

    init {
        this.view = view
        this.model = WeatherModel()
    }

    override fun startLoadingData(lat:Double, lon:Double) {

        // load Weather Data From Server
        model.loadWeatherData(lat, lon, object: WeatherCallback{
            override fun onLoadSuccess(weatherResponse: WeatherResponse?) {
                // Pass data to view
                view.onLoadFinished(weatherResponse)
            }

            override fun onLoadFailure(errorMessage: String?) {
                // Pass errorMessage to view
                view.onLoadFailed(errorMessage!!)
            }
        })
    }

}