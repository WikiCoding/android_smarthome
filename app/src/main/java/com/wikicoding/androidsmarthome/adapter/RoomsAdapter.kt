package com.wikicoding.androidsmarthome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wikicoding.androidsmarthome.databinding.RoomRvItemBinding
import com.wikicoding.androidsmarthome.model.RoomEntity

class RoomsAdapter(private val roomList: List<RoomEntity>) : RecyclerView.Adapter<RoomsAdapter.AdapterVH>() {
    private var onClicked: OnClickListen? = null

    inner class AdapterVH(binding: RoomRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvRoomName = binding.tvRoomName
        val rvRoomFloor = binding.tvRoomFloor
        val rvRoomArea = binding.tvRoomArea
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVH {
        return AdapterVH(RoomRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    override fun onBindViewHolder(holder: AdapterVH, position: Int) {
        val roomInstance = roomList[position]

        holder.rvRoomName.text = "Name: ${roomInstance.roomName}"
        holder.rvRoomFloor.text = "Floor: ${roomInstance.roomFloor}"
        holder.rvRoomArea.text = "Area: ${roomInstance.roomArea}"

        holder.itemView.setOnClickListener {
            if (onClicked != null) {
                val indexClicked = roomList.indexOf(roomInstance)
                onClicked!!.onClick(indexClicked, roomInstance)
            }
        }
    }

    interface OnClickListen {
        fun onClick(position: Int, roomInstance: RoomEntity)
    }

    fun setOnClick(onClick: OnClickListen) {
        this.onClicked = onClick
    }

    fun findSwipedItem(position: Int): RoomEntity {
        return roomList[position]
    }
}