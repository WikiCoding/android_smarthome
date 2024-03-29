package com.wikicoding.androidsmarthome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wikicoding.androidsmarthome.databinding.HomeRvItemBinding
import com.wikicoding.androidsmarthome.model.HomeEntity

class MainAdapter(private val homeList: List<HomeEntity>) : RecyclerView.Adapter<MainAdapter.MainAdapterVH>() {
    private var onClicked: OnClickListen? = null

    inner class MainAdapterVH(binding: HomeRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvHomeName = binding.tvHomeName
        val rvHomeAddress = binding.tvHomeAddress
        val rvHomeLat = binding.tvHomeLatitude
        val rvHomeLng = binding.tvHomeLongitude
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapterVH {
        return MainAdapterVH(HomeRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return homeList.size
    }

    override fun onBindViewHolder(holder: MainAdapterVH, position: Int) {
        val homeInstance = homeList[position]

        holder.rvHomeName.text = "Name: ${homeInstance.homeName}"
        holder.rvHomeAddress.text = "Address: ${homeInstance.address}"
        holder.rvHomeLat.text = "Latitude: ${homeInstance.lat}"
        holder.rvHomeLng.text = "Longitude: ${homeInstance.lng}"

        holder.itemView.setOnClickListener {
            if (onClicked != null) {
                val indexClicked = homeList.indexOf(homeInstance)
                onClicked!!.onClick(indexClicked, homeInstance)
            }
        }
    }

    interface OnClickListen {
        fun onClick(position: Int, homeInstance: HomeEntity)
    }

    fun setOnClick(onClick: OnClickListen) {
        this.onClicked = onClick
    }

    fun findSwipedItem(position: Int): HomeEntity {
        return homeList[position]
    }
}