package com.wikicoding.androidsmarthome.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey(autoGenerate = true)
    var roomId: Int = 0,
    var roomName: String = "",
    var roomFloor: String = "",
    var roomArea: Double = 0.0,
    var homeId: Int
): Serializable
