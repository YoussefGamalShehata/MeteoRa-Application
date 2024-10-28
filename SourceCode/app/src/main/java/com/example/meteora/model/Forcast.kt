package com.example.meteora.model
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import kotlin.collections.List
@Entity(tableName = "forecast_table")
data class Forcast(
    @PrimaryKey(autoGenerate = true)
    val key: Int = 0,
    val cod: String,
    val message: Long,
    val cnt: Long,
    @TypeConverters(ForecastConverters::class)
    val list: List<ForecastInfo>,
    @Embedded
    val city: ForecastCity,
)

data class ForecastInfo(
    val dt: Long,
    @Embedded
    val main: ForecastMain,
    @Embedded
    val weather: List<ForecastWeather>,
    @Embedded
    val clouds: ForecastClouds,
    @Embedded
    val wind: ForecastWind,
    val visibility: Long,
    val pop: Double,
    @Embedded
    val rain: ForecastRain?,
    @Embedded
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
    @Embedded
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
