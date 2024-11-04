package com.example.meteora.model

import com.google.gson.annotations.SerializedName

data class Weather(
    val coord: CurrentCoord = CurrentCoord(0.0, 0.0),
    val weather: List<CurrentWeather> = emptyList(),
    val base: String = "",
    val main: CurrentMain = CurrentMain(0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0),
    val visibility: Long = 0,
    val wind: CurrentWind = CurrentWind(0.0, 0, 0.0),
    val rain: CurrentRain = CurrentRain(0.0),
    val clouds: CurrentClouds = CurrentClouds(0),
    val dt: Long = 0,
    val sys: CurrentSys = CurrentSys("", 0, 0),
    val timezone: Long = 0,
    val id: Long = 0,
    val name: String = "",
    val cod: Long = 0
)

data class CurrentCoord(
    val lon: Double = 0.0,
    val lat: Double = 0.0
)

data class CurrentWeather(
    val id: Long = 0,
    val main: String = "",
    val description: String = "",
    val icon: String = ""
)

data class CurrentMain(
    val temp: Double = 0.0,
    @SerializedName("feels_like") val feelsLike: Double = 0.0,
    @SerializedName("temp_min") val tempMin: Double = 0.0,
    @SerializedName("temp_max") val tempMax: Double = 0.0,
    val pressure: Long = 0,
    val humidity: Long = 0,
    @SerializedName("sea_level") val seaLevel: Long = 0,
    @SerializedName("grnd_level") val grndLevel: Long = 0
)

data class CurrentWind(
    val speed: Double = 0.0,
    val deg: Long = 0,
    val gust: Double = 0.0
)

data class CurrentRain(
    @SerializedName("1h") val n1h: Double = 0.0
)

data class CurrentClouds(
    val all: Long = 0
)

data class CurrentSys(
    val country: String = "",
    val sunrise: Long = 0,
    val sunset: Long = 0
)
