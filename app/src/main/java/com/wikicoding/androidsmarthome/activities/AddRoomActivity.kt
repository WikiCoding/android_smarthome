package com.wikicoding.androidsmarthome.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.wikicoding.androidsmarthome.constants.Constants
import com.wikicoding.androidsmarthome.databinding.ActivityAddRoomBinding
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.RoomEntity
import kotlinx.coroutines.launch

class AddRoomActivity : BaseActivity() {
    private var binding: ActivityAddRoomBinding? = null
    private var currentHome: HomeEntity? = null
    private var currentRoom: RoomEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRoomBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(Constants.intentRoomEditExtra)) {
            currentRoom = intent.getSerializableExtra(Constants.intentRoomEditExtra) as RoomEntity?
            supportActionBar!!.title = "Edit Room ${currentRoom!!.roomName}"

            binding!!.btnAdd.text = "Update Room"
            binding!!.etRoomName.setText(currentRoom!!.roomName)
            binding!!.etRoomFloor.setText(currentRoom!!.roomFloor)
            binding!!.etRoomArea.setText(currentRoom!!.roomArea.toString())

            binding!!.btnAdd.setOnClickListener {
                val roomName = binding!!.etRoomName.text.toString()
                val roomFloor = binding!!.etRoomFloor.text.toString()
                val roomArea = binding!!.etRoomArea.text.toString()

                if (roomName.isEmpty() || roomName.isBlank() ||
                    roomFloor.isEmpty() || roomFloor.isBlank()
                ) {
                    dialogErrorFillingForm(this)
                    return@setOnClickListener
                }

                val room = RoomEntity(currentRoom!!.roomId, roomName.trim(), roomFloor.trim(), roomArea.toDouble(),
                    currentRoom!!.homeId)

                updateRoom(room)
                finish()
            }
        }

        if (intent.hasExtra(Constants.intentRoomExtra)) {
            supportActionBar!!.title = "Add ${Constants.intentRoomValueExtra}"

            currentHome = intent.getSerializableExtra(Constants.intentRoomExtra) as HomeEntity?

            binding!!.btnAdd.setOnClickListener {
                val roomName = binding!!.etRoomName.text.toString()
                val roomFloor = binding!!.etRoomFloor.text.toString()
                val roomArea = binding!!.etRoomArea.text.toString()

                if (roomName.isEmpty() || roomName.isBlank() ||
                    roomFloor.isEmpty() || roomFloor.isBlank()
                ) {
                    dialogErrorFillingForm(this)
                    return@setOnClickListener
                }

                val room = RoomEntity(0, roomName.trim(), roomFloor.trim(), roomArea.toDouble(),
                    currentHome!!.homeId)
                addRoom(room)
                finish()
            }
        }
    }

    private fun addRoom(roomEntity: RoomEntity) {
        lifecycleScope.launch {
            dao.addRoom(roomEntity)
        }
    }

    private fun updateRoom(room: RoomEntity) {
        lifecycleScope.launch {
            dao.updateRoom(room)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.e("home", currentHome.toString())
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