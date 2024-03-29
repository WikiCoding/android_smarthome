package com.wikicoding.androidsmarthome.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "home")
data class HomeEntity(
    @PrimaryKey(autoGenerate = true)
    var homeId: Int = 0,
    var homeName: String,
    var address: String,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
) : Serializable
