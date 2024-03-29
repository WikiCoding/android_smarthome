package com.wikicoding.androidsmarthome.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wikicoding.androidsmarthome.databinding.DeviceRvItemBinding
import com.wikicoding.androidsmarthome.model.DeviceEntity
import kotlin.random.Random

class DevicesAdapter(private val deviceList: List<DeviceEntity>) : RecyclerView.Adapter<DevicesAdapter.AdapterVH>() {
    private var onClicked: OnClickListen? = null

    inner class AdapterVH(binding: DeviceRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvDeviceName = binding.tvDeviceName
        val rvDeviceType = binding.tvDeviceType
        val rvDeviceInstantMeasurement = binding.tvDeviceInstantMeasurement
        val rvDeviceEnabled = binding.tvDeviceEnabled
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVH {
        return AdapterVH(DeviceRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    override fun onBindViewHolder(holder: AdapterVH, position: Int) {
        val deviceInstance = deviceList[position]

        holder.rvDeviceName.text = "Name: ${deviceInstance.deviceName}"
        holder.rvDeviceType.text = "Type: ${deviceInstance.deviceType}"

        if (deviceInstance.deviceEnabled) {
            holder.rvDeviceEnabled.setTextColor(Color.GREEN)
            holder.rvDeviceEnabled.text = "ENABLED"
            getMeasurement(deviceInstance.deviceType, holder)
        } else {
            holder.rvDeviceEnabled.setTextColor(Color.RED)
            holder.rvDeviceInstantMeasurement.visibility = View.INVISIBLE
            holder.rvDeviceEnabled.text = "DISABLED"
        }

        holder.itemView.setOnClickListener {
            if (onClicked != null) {
                val indexClicked = deviceList.indexOf(deviceInstance)
                onClicked!!.onClick(indexClicked, deviceInstance)
            }
        }
    }

    private fun getMeasurement(deviceType: String, holder: AdapterVH) {
        val tempLowerLimit = -2
        val tempUpperLimit = 34
        val randomTemp = Random.nextInt(tempLowerLimit, tempUpperLimit + 1)

        val humLowerLimit = 40
        val humUpperLimit = 98
        val randomHum = Random.nextInt(humLowerLimit, humUpperLimit + 1)
        when (deviceType.lowercase()) {
            "temperature" -> holder.rvDeviceInstantMeasurement.text = "Instant measurement: ${randomTemp}ºC"
            "humidity" -> holder.rvDeviceInstantMeasurement.text = "Instant measurement: ${randomHum}%"
            else -> holder.rvDeviceInstantMeasurement.text = "Measurement Not Supported"
        }
    }

    interface OnClickListen {
        fun onClick(position: Int, deviceInstance: DeviceEntity)
    }

    fun setOnClick(onClick: OnClickListen) {
        this.onClicked = onClick
    }

    fun findSwipedItem(position: Int): DeviceEntity {
        return deviceList[position]
    }
}