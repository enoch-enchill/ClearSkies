# ClearSkies
This is a simple weather app that can take location (country, state, city, town, zipcode, etc. and display some weather details. The app is written in Kotlin and uses the OpenWeatherMap APIs

## Google Places API
The app implements Google Places API's autocomplete service (https://developers.google.com/maps/documentation/places/android-sdk/autocomplete) to retieve the latitude and longitude of places that users input.

## OpenWeatherMap One Call API
The latitude and the longitude values retrieved from the Google Place API are then send to the OpenWeatherMap One-Call-API (https://openweathermap.org/api/one-call-api) to the query the current weather conditions (and those of the next 7 days) of the inputed location.

## App Screens
![Screen 01](https://github.com/et-enchill/ClearSkies/blob/master/screens/01.png) | 
![Screen 02](https://github.com/et-enchill/ClearSkies/blob/master/screens/02.png) | 
![Screen 03](https://github.com/et-enchill/ClearSkies/blob/master/screens/03.png) | 
![Screen 04](https://github.com/et-enchill/ClearSkies/blob/master/screens/04.png)
