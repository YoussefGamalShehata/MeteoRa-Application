package com.example.meteora.model
import com.example.meteora.model.City
import com.example.meteora.model.WeatherParam
import kotlin.collections.List

data class Forcast(
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: List<newParam>,
    val city: City,)
