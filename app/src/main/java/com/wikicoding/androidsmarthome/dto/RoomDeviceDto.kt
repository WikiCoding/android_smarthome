package com.wikicoding.androidsmarthome.dto

import java.io.Serializable

data class RoomDeviceDto(
    var roomId: Int = 0,
    var roomName: String = "",
    var deviceId: Int = 0,
    var deviceName: String = "",
    var deviceType: String = "",
    var deviceEnabled: Boolean = true,
): Serializable
