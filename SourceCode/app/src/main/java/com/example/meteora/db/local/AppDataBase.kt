package com.example.meteora.db.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.meteora.model.Forcast
import com.example.meteora.model.ForecastConverters

@Database(entities = [Forcast::class], version = 1, exportSchema = false)
@TypeConverters(ForecastConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): ForecastDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDataBase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "forecast_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}