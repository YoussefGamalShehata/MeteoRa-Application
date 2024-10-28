package com.example.meteora.db.local

import android.content.Context
import com.example.meteora.model.Forcast

class LocalDataSourceImpl(private val context : Context) : LocalDataSource {
    private val forecastDao = AppDatabase.getDataBase(context).weatherDao()

    override suspend fun insertForecast(forecast: Forcast) {
        forecastDao.insertForecast(forecast)
    }

    override suspend fun getAllForecast(): List<Forcast> {
        return forecastDao.getAllForecast()
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        forecastDao.deleteForecast(forecast)
    }

    override suspend fun updateForecast(forecast: Forcast) {
        forecastDao.updateForecast(forecast)
    }
}