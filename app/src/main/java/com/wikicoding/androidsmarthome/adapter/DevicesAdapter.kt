package com.wikicoding.androidsmarthome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wikicoding.androidsmarthome.databinding.DeviceRvItemBinding
import com.wikicoding.androidsmarthome.model.DeviceEntity
import com.wikicoding.androidsmarthome.model.RoomEntity

class DevicesAdapter(private val deviceList: List<DeviceEntity>) : RecyclerView.Adapter<DevicesAdapter.AdapterVH>() {
    private var onClicked: OnClickListen? = null

    inner class AdapterVH(binding: DeviceRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvRoomName = binding.tvDeviceName
        val rvRoomFloor = binding.tvDeviceType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVH {
        return AdapterVH(DeviceRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    override fun onBindViewHolder(holder: AdapterVH, position: Int) {
        val deviceInstance = deviceList[position]

        holder.rvRoomName.text = "Name: ${deviceInstance.deviceName}"
        holder.rvRoomFloor.text = "Floor: ${deviceInstance.deviceType}"

        holder.itemView.setOnClickListener {
            if (onClicked != null) {
                val indexClicked = deviceList.indexOf(deviceInstance)
                onClicked!!.onClick(indexClicked, deviceInstance)
            }
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