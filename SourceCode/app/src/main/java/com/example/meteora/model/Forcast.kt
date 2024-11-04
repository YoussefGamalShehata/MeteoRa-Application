package com.example.meteora.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName

@Entity(tableName = "forecast_table")
data class Forcast(
    @PrimaryKey(autoGenerate = true)
    val key: Int = 0,
    val cod: String = "",
    val message: Long = 0,
    val cnt: Long = 0,
    @TypeConverters(ForecastConverters::class)
    val list: List<ForecastInfo> = emptyList(),
    @Embedded
    val city: ForecastCity = ForecastCity()
)

data class ForecastInfo(
    val dt: Long = 0,
    @Embedded
    val main: ForecastMain = ForecastMain(),
    @Embedded
    val weather: List<ForecastWeather> = emptyList(),
    @Embedded
    val clouds: ForecastClouds = ForecastClouds(),
    @Embedded
    val wind: ForecastWind = ForecastWind(),
    val visibility: Long = 0,
    val pop: Double = 0.0,
    @Embedded
    val rain: ForecastRain? = null,
    @Embedded
    val sys: ForecastSys = ForecastSys(),
    @SerializedName("dt_txt")
    val dtTxt: String = ""
)

data class ForecastMain(
    val temp: Double = 0.0,
    @SerializedName("feels_like") val feelsLike: Double = 0.0,
    @SerializedName("temp_min") val tempMin: Double = 0.0,
    @SerializedName("temp_max") val tempMax: Double = 0.0,
    val pressure: Long = 0,
    @SerializedName("sea_level") val seaLevel: Long = 0,
    @SerializedName("grnd_level") val grndLevel: Long = 0,
    val humidity: Long = 0,
    @SerializedName("temp_kf") val tempKf: Double = 0.0
)

data class ForecastWeather(
    val id: Long = 0,
    val main: String = "",
    val description: String = "",
    val icon: String = ""
)

data class ForecastClouds(
    val all: Long = 0
)

data class ForecastWind(
    val speed: Double = 0.0,
    val deg: Long = 0,
    val gust: Double = 0.0
)

data class ForecastRain(
    @SerializedName("3h") val n3h: Double = 0.0
)

data class ForecastSys(
    val pod: String = ""
)

data class ForecastCity(
    val id: Long = 0,
    val name: String = "",
    @Embedded val coord: ForecastCoord = ForecastCoord(),
    val country: String = "",
    val population: Long = 0,
    val timezone: Long = 0,
    val sunrise: Long = 0,
    val sunset: Long = 0
)

data class ForecastCoord(
    val lat: Double = 0.0,
    val lon: Double = 0.0
)
