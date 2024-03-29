package com.wikicoding.androidsmarthome.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wikicoding.androidsmarthome.dao.SmartHomeDao
import com.wikicoding.androidsmarthome.model.DeviceEntity
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.RoomEntity

@Database(entities = [HomeEntity::class, RoomEntity::class, DeviceEntity::class], version = 1)
abstract class SmartHomeDb: RoomDatabase() {

    abstract fun smartHomeDao(): SmartHomeDao

    companion object {
        @Volatile
        private var INSTANCE: SmartHomeDb? = null

        fun getInstance(context: Context): SmartHomeDb {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SmartHomeDb::class.java,
                        "smart_home_db"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}