package com.wikicoding.androidsmarthome.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import com.wikicoding.androidsmarthome.R
import com.wikicoding.androidsmarthome.constants.Constants
import com.wikicoding.androidsmarthome.databinding.ActivityAddDeviceBinding
import com.wikicoding.androidsmarthome.model.DeviceEntity
import com.wikicoding.androidsmarthome.model.RoomEntity
import kotlinx.coroutines.launch

class AddDeviceActivity : BaseActivity(), AdapterView.OnItemSelectedListener {
    private var binding: ActivityAddDeviceBinding? = null
    private var currentRoom: RoomEntity? = null
    private var currentDevice: DeviceEntity? = null
    private var deviceTypeDropdown: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setupDeviceTypeDropdownMenu()

        if (intent.hasExtra(Constants.intentDeviceEditExtra)) {
            currentDevice =
                intent.getSerializableExtra(Constants.intentDeviceEditExtra) as DeviceEntity?

            supportActionBar!!.title = "Edit Device ${currentDevice!!.deviceName}"

            binding!!.btnAdd.text = "Edit Device"
            binding!!.etDeviceName.setText(currentDevice!!.deviceName)
            binding!!.etDeviceType.setText(currentDevice!!.deviceType)
            deviceTypeDropdown = currentDevice!!.deviceType

            binding!!.btnAdd.setOnClickListener {
                val deviceName = binding!!.etDeviceName.text.toString()
                val deviceType = binding!!.etDeviceType.text.toString()

                if (deviceName.isEmpty() || deviceName.isBlank() ||
                    deviceType.isEmpty() || deviceType.isBlank()
                ) {
                    dialogErrorFillingForm(this)
                    return@setOnClickListener
                }

                val device = DeviceEntity(
                    currentDevice!!.deviceId, deviceName.trim(), deviceType.trim(),
                    currentDevice!!.deviceEnabled, currentDevice!!.roomId
                )
                updateDevice(device)
                finish()
            }
        }

        if (intent.hasExtra(Constants.intentRoomExtra)) {
            currentRoom = intent.getSerializableExtra(Constants.intentRoomExtra) as RoomEntity?

            supportActionBar!!.title = "Add to ${currentRoom!!.roomName}"

            binding!!.btnAdd.setOnClickListener {
                val deviceName = binding!!.etDeviceName.text.toString()
                val deviceType: String? = if (deviceTypeDropdown == "None") binding!!.etDeviceType.text.toString()
                    else deviceTypeDropdown

                if (deviceName.isEmpty() || deviceName.isBlank() ||
                    deviceType!!.isEmpty() || deviceType.isBlank()
                ) {
                    dialogErrorFillingForm(this)
                    return@setOnClickListener
                }

                val device = DeviceEntity(
                    0,
                    deviceName.trim(),
                    deviceType.trim(),
                    true,
                    currentRoom!!.roomId
                )
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

    private fun setupDeviceTypeDropdownMenu() {
        /** adding dropdown elements see https://developer.android.com/develop/ui/views/components/spinner?hl=pt-br **/
        val dropdown: Spinner = binding!!.dropdownDeviceType
        dropdown.onItemSelectedListener = this
        /** added a resource in the strings.xml file **/
        ArrayAdapter.createFromResource(
            this,
            R.array.device_type_array,
            android.R.layout.simple_spinner_dropdown_item // pointing to the layout
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dropdown.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        deviceTypeDropdown = parent?.getItemAtPosition(position) as String?
        if (deviceTypeDropdown != "None") binding!!.etDeviceType.setText(deviceTypeDropdown)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
