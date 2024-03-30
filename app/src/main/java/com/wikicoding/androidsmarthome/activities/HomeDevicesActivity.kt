package com.wikicoding.androidsmarthome.activities

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wikicoding.androidsmarthome.adapter.HomeDevicesAdapter
import com.wikicoding.androidsmarthome.constants.Constants
import com.wikicoding.androidsmarthome.databinding.ActivityHomeDevicesBinding
import com.wikicoding.androidsmarthome.dto.RoomDeviceDto
import com.wikicoding.androidsmarthome.model.DeviceEntity
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.relations.RoomWithDevices
import kotlinx.coroutines.launch

class HomeDevicesActivity : BaseActivity() {
    private var binding: ActivityHomeDevicesBinding? = null
    private var currentHome: HomeEntity? = null
    private var adapter: HomeDevicesAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDevicesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.intentHomeExtra)) {
            currentHome = intent.getSerializableExtra(Constants.intentHomeExtra) as HomeEntity?

            getData()
        }

    }

    private fun getData() {
        lifecycleScope.launch {
            val roomWithDevices = dao.findAllDevicesByHomeId(currentHome!!.homeId)

            val dtoList: ArrayList<RoomDeviceDto> = buildDto(roomWithDevices)

            setupHomeDevicesRv(dtoList)
        }
    }

    private fun setupHomeDevicesRv(devices: ArrayList<RoomDeviceDto>) {
        adapter = HomeDevicesAdapter(devices)
        binding!!.rvHomeDevices.layoutManager = LinearLayoutManager(this)
        binding!!.rvHomeDevices.adapter = adapter

        adapter!!.setOnClick(object : HomeDevicesAdapter.OnClickListen {
            override fun onClick(position: Int, deviceInstance: RoomDeviceDto) {
                val indexOfClickedItem = devices.indexOf(deviceInstance)
                devices[indexOfClickedItem].deviceEnabled = !devices[indexOfClickedItem].deviceEnabled

                // build deviceEntity
                val device = DeviceEntity(devices[position].deviceId, devices[position].deviceName,
                    devices[position].deviceType, devices[position].deviceEnabled, devices[position].roomId)

                updateDevice(device)

                adapter!!.notifyItemChanged(position)
            }
        })
    }

    private fun buildDto(roomWithDevices: List<RoomWithDevices>): ArrayList<RoomDeviceDto> {
        val dtoList: ArrayList<RoomDeviceDto> = arrayListOf();

        var index = 0
        for (roomWithDevice in roomWithDevices) {
            if (index == 0) {
                for (device in roomWithDevice.devices) {
                    val dto = RoomDeviceDto(roomWithDevice.rooms.roomId, roomWithDevice.rooms.roomName,
                        device.deviceId, device.deviceName, device.deviceType, device.deviceEnabled)
                    dtoList.add(dto)
                }
            }
            if (index > 0) {
                if (roomWithDevice.rooms.roomId != roomWithDevices[index - 1].rooms.roomId) {
                    for (device in roomWithDevice.devices) {
                        val dto = RoomDeviceDto(roomWithDevice.rooms.roomId, roomWithDevice.rooms.roomName,
                            device.deviceId, device.deviceName, device.deviceType, device.deviceEnabled)
                        dtoList.add(dto)
                    }
                }
            }
            index++
        }

        return dtoList
    }

    private fun updateDevice(deviceEntity: DeviceEntity) {
        lifecycleScope.launch {
            dao.updateDevice(deviceEntity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}