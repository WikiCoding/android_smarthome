package com.wikicoding.androidsmarthome.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.wikicoding.androidsmarthome.R
import com.wikicoding.androidsmarthome.constants.Constants
import com.wikicoding.androidsmarthome.databinding.ActivityAddDeviceBinding
import com.wikicoding.androidsmarthome.databinding.ActivityAddRoomBinding
import com.wikicoding.androidsmarthome.databinding.ActivityDeviceBinding
import com.wikicoding.androidsmarthome.model.DeviceEntity
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.RoomEntity
import kotlinx.coroutines.launch

class AddDeviceActivity : BaseActivity() {
    private var binding: ActivityAddDeviceBinding? = null
    private var currentRoom: RoomEntity? = null
    private var currentDevice: DeviceEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(Constants.intentDeviceEditExtra)) {
            currentDevice = intent.getSerializableExtra(Constants.intentDeviceEditExtra) as DeviceEntity?

            supportActionBar!!.title = "Edit Device ${currentDevice!!.deviceName}"

            binding!!.btnAdd.text = "Edit Device"
            binding!!.etDeviceName.setText(currentDevice!!.deviceName)
            binding!!.etDeviceType.setText(currentDevice!!.deviceType)

            binding!!.btnAdd.setOnClickListener {
                val deviceName = binding!!.etDeviceName.text.toString()
                val deviceType = binding!!.etDeviceType.text.toString()

                if (deviceName.isEmpty() || deviceName.isBlank() ||
                    deviceType.isEmpty() || deviceType.isBlank()
                ) {
                    dialogErrorFillingForm(this)
                    return@setOnClickListener
                }

                val device = DeviceEntity(currentDevice!!.deviceId, deviceName.trim(), deviceType.trim(),
                    currentDevice!!.deviceEnabled, currentDevice!!.roomId)
                updateDevice(device)
                finish()
            }
        }

        if (intent.hasExtra(Constants.intentRoomExtra)) {
            supportActionBar!!.title = "Add to ${Constants.intentRoomValueExtra}"

            currentRoom = intent.getSerializableExtra(Constants.intentRoomExtra) as RoomEntity?

            binding!!.btnAdd.setOnClickListener {
                val deviceName = binding!!.etDeviceName.text.toString()
                val deviceType = binding!!.etDeviceType.text.toString()

                if (deviceName.isEmpty() || deviceName.isBlank() ||
                    deviceType.isEmpty() || deviceType.isBlank()
                ) {
                    dialogErrorFillingForm(this)
                    return@setOnClickListener
                }

                val device = DeviceEntity(0, deviceName.trim(), deviceType.trim(), true, currentRoom!!.roomId)
                addDevice(device)
                finish()
            }
        }
    }

    private fun updateDevice(device: DeviceEntity) {
        lifecycleScope.launch {
            dao.updateDevice(device)
        }
    }

    private fun addDevice(device: DeviceEntity) {
        lifecycleScope.launch {
            dao.addDevice(device)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}