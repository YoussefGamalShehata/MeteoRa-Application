package com.example.meteora.model

data class Weather(
    val coord: Coord,
    val weather: List<WeatherParam>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long,
)

data class Coord(
    val lon: Double,
    val lat: Double,
)

data class WeatherParam(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String,
)

data class Main(
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Long,
    val humidity: Long,
    val seaLevel: Long,
    val grndLevel: Long,
)

data class Wind(
    val speed: Double,
    val deg: Long,
    val gust: Double,
)

data class Clouds(
    val all: Long,
)

data class Sys(
    val type: Long,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
)