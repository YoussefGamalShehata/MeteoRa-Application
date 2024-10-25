package com.example.meteora.model
import com.google.gson.annotations.SerializedName
import kotlin.collections.List

data class Forcast(
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: List<ForecastInfo>,
    val city: ForecastCity,
)

data class ForecastInfo(
    val dt: Long,
    val main: ForecastMain,
    val weather: List<ForecastWeather>,
    val clouds: ForecastClouds,
    val wind: ForecastWind,
    val visibility: Long,
    val pop: Double,
    val rain: ForecastRain?,
    val sys: ForecastSys,
    @SerializedName("dt_txt")
    val dtTxt: String,
)

data class ForecastMain(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Long,
    @SerializedName("sea_level")
    val seaLevel: Long,
    @SerializedName("grnd_level")
    val grndLevel: Long,
    val humidity: Long,
    @SerializedName("temp_kf")
    val tempKf: Double,
)

data class ForecastWeather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String,
)

data class ForecastClouds(
    val all: Long,
)

data class ForecastWind(
    val speed: Double,
    val deg: Long,
    val gust: Double,
)

data class ForecastRain(
    @SerializedName("3h")
    val n3h: Double,
)

data class ForecastSys(
    val pod: String,
)

data class ForecastCity(
    val id: Long,
    val name: String,
    val coord: ForecastCoord,
    val country: String,
    val population: Long,
    val timezone: Long,
    val sunrise: Long,
    val sunset: Long,
)

data class ForecastCoord(
    val lat: Double,
    val lon: Double,
)
