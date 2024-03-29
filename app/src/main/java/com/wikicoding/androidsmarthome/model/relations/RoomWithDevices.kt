package com.wikicoding.androidsmarthome.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.wikicoding.androidsmarthome.model.DeviceEntity
import com.wikicoding.androidsmarthome.model.RoomEntity

data class RoomWithDevices(
    @Embedded val rooms: RoomEntity, // for the main table that all the others will be linked to as 1 to n relationship
    @Relation(
        parentColumn = "roomId", // PK from the VehicleEntity table
        entityColumn = "roomId" // FK from the LogEntity table
    )
    val devices: List<DeviceEntity>
)
