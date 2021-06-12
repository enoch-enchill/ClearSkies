package com.joejoenchill.clearskies.weather

import com.joejoenchill.clearskies.data.RetrofitClient
import com.joejoenchill.clearskies.models.WeatherResponse
import com.joejoenchill.clearskies.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherModel : WeatherContract.Model{

    override fun loadWeatherData(lat:Double, lon:Double, callback: WeatherCallback) {
        RetrofitClient.getWeatherService().getWeatherData(lat ,lon , Constants.UNIT_NAME, Constants.EXCLUDE, Constants.API_KEY)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    callback.onLoadFailure(t.message)
                }

                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful){
                        callback.onLoadSuccess(response.body())
                    }
                }
            })
    }
}