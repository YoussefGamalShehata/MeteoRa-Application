package com.example.meteora.model

import com.google.gson.annotations.SerializedName

//data class Weather(
//    val coord: Coord,
//    val list: List<newParam>,
//    val base: String,
//    val main: Main,
//    val visibility: Long,
//    val wind: Wind,
//    val clouds: Clouds,
//    val dt: Long,
//    val sys: Sys,
//    val timezone: Long,
//    val id: Long,
//    val name: String,
//    val cod: Long,
//)
//
//data class newParam(
//    val dt: Long,
//    val main: Main,
//    val weather: List<WeatherParam>,
//    val clouds: Clouds,
//    val wind: Wind,
//    val visibility: Long,
//    val pop: Long,
//    val sys: Sys,
//    @SerializedName("dt_txt")
//    val dtTxt: String,
//)
//
//data class Main(
//    val temp: Double,
//    @SerializedName("feels_like")
//    val feelsLike: Double,
//    @SerializedName("temp_min")
//    val tempMin: Double,
//    @SerializedName("temp_max")
//    val tempMax: Double,
//    val pressure: Long,
//    @SerializedName("sea_level")
//    val seaLevel: Long,
//    @SerializedName("grnd_level")
//    val grndLevel: Long,
//    val humidity: Long,
//    @SerializedName("temp_kf")
//    val tempKf: Double,
//)
//
//data class WeatherParam(
//    val id: Long,
//    val main: String,
//    val description: String,
//    val icon: String,
//)
//
//data class Clouds(
//    val all: Long,
//)
//
//data class Wind(
//    val speed: Double,
//    val deg: Long,
//    val gust: Double,
//)
//
//data class Sys(
//    val pod: String,
//)
//
//data class City(
//    val id: Long,
//    val name: String,
//    val coord: Coord,
//    val country: String,
//    val population: Long,
//    val timezone: Long,
//    val sunrise: Long,
//    val sunset: Long,
//)
//
//data class Coord(
//    val lat: Double,
//    val lon: Double,
//)
data class Weather(
    val coord: CurrentCoord,
    val weather: List<CurrentWeather>,
    val base: String,
    val main: CurrentMain,
    val visibility: Long,
    val wind: CurrentWind,
    val rain: CurrentRain,
    val clouds: CurrentClouds,
    val dt: Long,
    val sys: CurrentSys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long,
)

data class CurrentCoord(
    val lon: Double,
    val lat: Double,
)

data class CurrentWeather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String,
)

data class CurrentMain(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Long,
    val humidity: Long,
    @SerializedName("sea_level")
    val seaLevel: Long,
    @SerializedName("grnd_level")
    val grndLevel: Long,
)

data class CurrentWind(
    val speed: Double,
    val deg: Long,
    val gust: Double,
)

data class CurrentRain(
    @SerializedName("1h")
    val n1h: Double,
)

data class CurrentClouds(
    val all: Long,
)

data class CurrentSys(
    val country: String,
    val sunrise: Long,
    val sunset: Long,
)