package com.wikicoding.androidsmarthome.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wikicoding.androidsmarthome.adapter.MainAdapter
import com.wikicoding.androidsmarthome.adapter.RoomsAdapter
import com.wikicoding.androidsmarthome.constants.Constants
import com.wikicoding.androidsmarthome.databinding.ActivityRoomBinding
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.RoomEntity
import com.wikicoding.androidsmarthome.model.relations.HomeWithRooms
import com.wikicoding.explorelog.utils.SwipeToDeleteCallback
import com.wikicoding.explorelog.utils.SwipeToEditCallback
import kotlinx.coroutines.launch

class RoomActivity : BaseActivity() {
    private var binding: ActivityRoomBinding? = null
    private var currentHome: HomeEntity? = null
    private var dbList: ArrayList<HomeWithRooms>? = null
    private var roomsList: ArrayList<RoomEntity>? = null
    private var adapter: RoomsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

//        if (intent.hasExtra(Constants.intentDeviceEditExtra)) {
//            val homeId = intent.getIntExtra(Constants.intentDeviceEditExtra, 0)
//            lifecycleScope.launch {
//                currentHome = dao.findHomeById(homeId)
//            }
//        }

        if (intent.hasExtra(Constants.intentRoomExtra)) {
            currentHome = intent.getSerializableExtra(Constants.intentRoomExtra) as HomeEntity?
        }


//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = currentHome!!.homeName

        findRoomsByHouseId(currentHome!!.homeId)

        binding!!.btnAddRoom.setOnClickListener {
            val intent = Intent(this, AddRoomActivity::class.java)
            intent.putExtra(Constants.intentRoomExtra, currentHome)
            startActivity(intent)
        }

        handleDeleteSwipe()
        handleEditSwipe()
    }

    private fun findRoomsByHouseId(idHouse: Int) {
        lifecycleScope.launch {
            dbList = dao.findRoomsByHouseId(idHouse) as ArrayList<HomeWithRooms>

            if (dbList!![0].rooms.isEmpty()) {
                roomsList = arrayListOf()
            } else {
                roomsList = dbList!![0].rooms as ArrayList<RoomEntity>
            }
            setupRoomsRv(roomsList!!)
        }
    }

    private fun setupRoomsRv(roomsList: ArrayList<RoomEntity>) {
        adapter = RoomsAdapter(roomsList)
        binding!!.rvRooms.layoutManager = LinearLayoutManager(this)
        binding!!.rvRooms.adapter = adapter

        adapter!!.setOnClick(object : RoomsAdapter.OnClickListen {
            override fun onClick(position: Int, roomInstance: RoomEntity) {
                val indexOfClickedItem = roomsList.indexOf(roomInstance)
                val intent = Intent(this@RoomActivity, DeviceActivity::class.java)
                intent.putExtra(Constants.intentRoomExtra, roomsList[indexOfClickedItem])
                startActivity(intent)
            }
        })
    }

    private fun handleDeleteSwipe() {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val rvAdapter = binding!!.rvRooms.adapter as RoomsAdapter
                val itemToDelete = rvAdapter.findSwipedItem(viewHolder.adapterPosition)
                deleteConfirmationDialog(
                    this@RoomActivity, null, itemToDelete, null, null,
                    roomsList!!, null, null, adapter, null,
                    viewHolder.adapterPosition)
            }
        }

        val deleteItemTouchHandler = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHandler.attachToRecyclerView(binding!!.rvRooms)
    }

    private fun handleEditSwipe() {
        val editItemSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val rvAdapter = binding!!.rvRooms.adapter as RoomsAdapter
                val itemToEdit: RoomEntity = rvAdapter.findSwipedItem(viewHolder.adapterPosition)

                val intent = Intent(this@RoomActivity, AddRoomActivity::class.java)
                intent.putExtra(Constants.intentRoomEditExtra, itemToEdit)
                startActivity(intent)
            }
        }

        val editItemTouchHandler = ItemTouchHelper(editItemSwipeHandler)
        editItemTouchHandler.attachToRecyclerView(binding!!.rvRooms)
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
        findRoomsByHouseId(currentHome!!.homeId)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}