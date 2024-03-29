package com.wikicoding.androidsmarthome.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true)
    var deviceId: Int = 0,
    var deviceName: String = "",
    var deviceType: String = "",
    var roomId: Int
)
