package com.wikicoding.androidsmarthome.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wikicoding.androidsmarthome.model.DeviceEntity
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.RoomEntity
import com.wikicoding.androidsmarthome.model.relations.HomeWithRooms
import com.wikicoding.androidsmarthome.model.relations.RoomWithDevices

@Dao
interface SmartHomeDao {
    @Query("SELECT * FROM home ORDER BY homeName ASC")
    suspend fun findAllHomes(): List<HomeEntity>

    @Query("SELECT * FROM home WHERE homeId = :homeId")
    suspend fun findHomeById(homeId: Int): HomeEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHome(homeEntity: HomeEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHome(home: HomeEntity)

    @Delete
    suspend fun deleteHome(homeEntity: HomeEntity)

    @Transaction
    @Query("SELECT * FROM home WHERE homeId= :homeId")
    suspend fun findRoomsByHouseId(homeId: Int): List<HomeWithRooms>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRoom(room: RoomEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRoom(room: RoomEntity)

    @Delete
    suspend fun deleteRoom(room: RoomEntity)

    @Transaction
    @Query("SELECT * FROM rooms WHERE roomId= :roomId")
    suspend fun findDevicesByRoomId(roomId: Int): List<RoomWithDevices>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDevice(device: DeviceEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDevice(device: DeviceEntity)

    @Delete
    suspend fun deleteDevice(device: DeviceEntity)

    @Transaction
    @Query("SELECT * FROM home " +
            "JOIN rooms ON home.homeId = rooms.homeId " +
            "JOIN devices ON devices.roomId = rooms.roomId WHERE home.homeId = :homeId ORDER BY rooms.roomId")
    suspend fun findAllDevicesByHomeId(homeId: Int): List<RoomWithDevices>
}