package com.wikicoding.androidsmarthome.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wikicoding.androidsmarthome.adapter.DevicesAdapter
import com.wikicoding.androidsmarthome.adapter.RoomsAdapter
import com.wikicoding.androidsmarthome.constants.Constants
import com.wikicoding.androidsmarthome.databinding.ActivityDeviceBinding
import com.wikicoding.androidsmarthome.model.DeviceEntity
import com.wikicoding.androidsmarthome.model.RoomEntity
import com.wikicoding.androidsmarthome.model.relations.RoomWithDevices
import kotlinx.coroutines.launch

class DeviceActivity : BaseActivity() {
    private var binding: ActivityDeviceBinding? = null
    private var currentRoom: RoomEntity? = null
    private var dbList: ArrayList<RoomWithDevices>? = null
    private var devicesList: ArrayList<DeviceEntity>? = null
    private var adapter: DevicesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.intentRoomExtra)) {
            currentRoom = intent.getSerializableExtra(Constants.intentRoomExtra) as RoomEntity?
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = currentRoom!!.roomName

        findAllDevicesByRoomId(currentRoom!!.roomId)

        binding!!.btnAddDevice.setOnClickListener {
            val intent = Intent(this, AddDeviceActivity::class.java)
            intent.putExtra(Constants.intentRoomExtra, currentRoom)
            startActivity(intent)
        }
    }

    private fun findAllDevicesByRoomId(roomId: Int) {
        lifecycleScope.launch {
            dbList = dao.findDevicesByRoomId(roomId) as ArrayList<RoomWithDevices>

            if (dbList!![0].devices.isEmpty()) {
                devicesList = arrayListOf()
            } else {
                devicesList = dbList!![0].devices as ArrayList<DeviceEntity>
            }
            setupDevicesRv(devicesList!!)
        }
    }

    private fun setupDevicesRv(devicesList: ArrayList<DeviceEntity>) {
        adapter = DevicesAdapter(devicesList)
        binding!!.rvDevices.layoutManager = LinearLayoutManager(this)
        binding!!.rvDevices.adapter = adapter

        adapter!!.setOnClick(object : DevicesAdapter.OnClickListen {
            override fun onClick(position: Int, deviceInstance: DeviceEntity) {
                val indexOfClickedItem = devicesList.indexOf(deviceInstance)
                // TODO implement go to the next screen
                val intent = null
            }
        })
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        findAllDevicesByRoomId(currentRoom!!.roomId)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}