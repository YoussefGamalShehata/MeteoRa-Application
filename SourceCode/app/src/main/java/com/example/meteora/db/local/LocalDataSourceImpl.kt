package com.example.meteora.db.local

import android.content.Context
import com.example.meteora.model.Forcast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSourceImpl(private val context : Context) : LocalDataSource {
    private val forecastDao = AppDatabase.getDataBase(context).weatherDao()

    override suspend fun insertForecast(forecast: Forcast) {
        withContext(Dispatchers.IO) {
            forecastDao.insertForecast(forecast)
        }
    }

    override suspend fun getAllForecast(): List<Forcast> {
        return withContext(Dispatchers.IO) {
            forecastDao.getAllForecast()
        }
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        withContext(Dispatchers.IO) {
            forecastDao.deleteForecast(forecast)
        }
    }

    override suspend fun updateForecast(forecast: Forcast) {
        withContext(Dispatchers.IO) {
            forecastDao.updateForecast(forecast)
        }
    }
}