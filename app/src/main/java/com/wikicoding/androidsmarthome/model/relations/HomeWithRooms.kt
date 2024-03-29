package com.wikicoding.androidsmarthome.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.RoomEntity

data class HomeWithRooms(
    @Embedded val homes: HomeEntity, // for the main table that all the others will be linked to as 1 to n relationship
    @Relation(
        parentColumn = "homeId", // PK from the VehicleEntity table
        entityColumn = "homeId" // FK from the LogEntity table
    )
    val rooms: List<RoomEntity>
)
