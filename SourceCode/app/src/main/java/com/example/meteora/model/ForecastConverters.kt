package com.example.meteora.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ForecastConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromForecastInfoList(value: List<ForecastInfo>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toForecastInfoList(value: String): List<ForecastInfo> {
        val type = object : TypeToken<List<ForecastInfo>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromForecastCity(value: ForecastCity): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toForecastCity(value: String): ForecastCity {
        return gson.fromJson(value, ForecastCity::class.java)
    }

}