package com.example.meteora.db.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.meteora.model.Forcast

@Dao
interface ForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: Forcast)

    @Query("SELECT * FROM forecast_table")
    suspend fun getAllForecast(): List<Forcast>

    @Delete // Use Room's Delete annotation
    suspend fun deleteForecast(forecast: Forcast)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateForecast(forecast: Forcast)
}
