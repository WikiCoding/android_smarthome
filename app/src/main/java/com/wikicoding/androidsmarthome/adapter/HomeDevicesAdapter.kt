package com.wikicoding.androidsmarthome.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wikicoding.androidsmarthome.databinding.HomeDeviceRvItemBinding
import com.wikicoding.androidsmarthome.dto.RoomDeviceDto
import com.wikicoding.androidsmarthome.model.relations.RoomWithDevices
import kotlin.random.Random

class HomeDevicesAdapter(private val homeDevicesList: List<RoomDeviceDto>) : RecyclerView.Adapter<HomeDevicesAdapter.AdapterVH>() {
    private var onClicked: HomeDevicesAdapter.OnClickListen? = null
    inner class AdapterVH(binding: HomeDeviceRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvRoomName = binding.tvRoomName
        val rvDeviceName = binding.tvDeviceName
        val rvDeviceType = binding.tvDeviceType
        val rvDeviceInstantMeasurement = binding.tvDeviceInstantMeasurement
        val rvDeviceEnabled = binding.tvDeviceEnabled
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVH {
        return AdapterVH(HomeDeviceRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return homeDevicesList.size
    }

    override fun onBindViewHolder(holder: AdapterVH, position: Int) {
        val instance = homeDevicesList[position]

        holder.rvRoomName.text = "Room Name: ${instance.roomName}"
        holder.rvDeviceName.text = "Name: ${instance.deviceName}"
        holder.rvDeviceType.text = "Type: ${instance.deviceName}"

        if (instance.deviceEnabled) {
            holder.rvDeviceEnabled.setTextColor(Color.GREEN)
            holder.rvDeviceEnabled.text = "ENABLED"
            getMeasurement(instance.deviceType, holder)
        } else {
            holder.rvDeviceEnabled.setTextColor(Color.RED)
            holder.rvDeviceInstantMeasurement.visibility = View.INVISIBLE
            holder.rvDeviceEnabled.text = "DISABLED"
        }

        holder.itemView.setOnClickListener {
            if (onClicked != null) {
                val indexClicked = homeDevicesList.indexOf(instance)
                onClicked!!.onClick(indexClicked, instance)
            }
        }
    }

    interface OnClickListen {
        fun onClick(position: Int, deviceInstance: RoomDeviceDto)
    }

    fun setOnClick(onClick: OnClickListen) {
        this.onClicked = onClick
    }

    fun findSwipedItem(position: Int): RoomDeviceDto {
        return homeDevicesList[position]
    }

    private fun getMeasurement(deviceType: String, holder: HomeDevicesAdapter.AdapterVH) {
        val tempLowerLimit = -2
        val tempUpperLimit = 34
        val randomTemp = Random.nextInt(tempLowerLimit, tempUpperLimit + 1)

        val humLowerLimit = 40
        val humUpperLimit = 98
        val randomHum = Random.nextInt(humLowerLimit, humUpperLimit + 1)

        val speedLowerLimit = 0
        val speedUpperLimit = 49
        val randomSpeed = Random.nextInt(speedLowerLimit, speedUpperLimit + 1)

        val percentLowerLimit = 0
        val percentUpperLimit = 99
        val randomPercent = Random.nextInt(percentLowerLimit, percentUpperLimit + 1)

        val detectionVal = listOf("Not Detected", "Detected")
        val connectionVal = listOf("Connected", "Not Connected")
        val randomBinary = Random.nextInt(0, 2)
        when (deviceType.lowercase()) {
            "temperature" -> holder.rvDeviceInstantMeasurement.text = "Instant measurement: ${randomTemp}ºC"
            "humidity" -> holder.rvDeviceInstantMeasurement.text = "Instant measurement: ${randomHum}%"
            "presence" -> holder.rvDeviceInstantMeasurement.text = "Instant measurement: ${detectionVal[randomBinary]}"
            "on off" -> holder.rvDeviceInstantMeasurement.text = "Instant measurement: ${connectionVal[randomBinary]}"
            "speed" -> holder.rvDeviceInstantMeasurement.text = "Instant measurement: ${randomSpeed}km/h"
            "light dimmer" -> holder.rvDeviceInstantMeasurement.text = "Instant measurement: ${randomPercent}%"
            else -> holder.rvDeviceInstantMeasurement.text = "Measurement Not Supported"
        }
    }
}