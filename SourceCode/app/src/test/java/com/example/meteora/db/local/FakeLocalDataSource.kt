package com.example.meteora.db.local

import com.example.meteora.model.Forcast

class FakeLocalDataSource : LocalDataSource
{
    override suspend fun insertForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllForecast(): List<Forcast> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }

    override suspend fun updateForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }

}