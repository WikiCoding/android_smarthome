package com.wikicoding.androidsmarthome.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wikicoding.androidsmarthome.adapter.MainAdapter
import com.wikicoding.androidsmarthome.constants.Constants
import com.wikicoding.androidsmarthome.databinding.ActivityMainBinding
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.explorelog.utils.SwipeToDeleteCallback
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private var binding: ActivityMainBinding? = null
    private var homeList: ArrayList<HomeEntity>? = null
    private var adapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        title = "Your houses"

        findAllHomes()

        binding!!.btnAddHome.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            intent.putExtra(Constants.intentHomeExtra, Constants.intentHomeValueExtra)
            startActivity(intent)
        }

        handleDeleteSwipe()

        homeList = arrayListOf()
        homeRvSetup(homeList!!)
    }

    private fun findAllHomes() {
        lifecycleScope.launch {
            homeList = dao.findAllHomes() as ArrayList<HomeEntity>
            homeRvSetup(homeList!!)
        }
    }

    private fun homeRvSetup(homeList: ArrayList<HomeEntity>) {
        adapter = MainAdapter(homeList)
        binding!!.rvHomes.layoutManager = LinearLayoutManager(this)
        binding!!.rvHomes.adapter = adapter

        adapter!!.setOnClick(object : MainAdapter.OnClickListen {
            override fun onClick(position: Int, homeInstance: HomeEntity) {
                val indexOfClickedItem = homeList.indexOf(homeInstance)
                val intent = Intent(applicationContext, RoomActivity::class.java)
                intent.putExtra(Constants.intentRoomExtra, homeList[indexOfClickedItem])
                startActivity(intent)
            }

        })
    }

    private fun handleDeleteSwipe() {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val rvAdapter = binding!!.rvHomes.adapter as MainAdapter
                val itemToDelete = rvAdapter.findSwipedItem(viewHolder.adapterPosition)
                deleteConfirmationDialog(this@MainActivity, itemToDelete, null,
                    homeList!!, null, adapter, null, viewHolder.adapterPosition)
            }
        }

        val deleteItemTouchHandler = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHandler.attachToRecyclerView(binding!!.rvHomes)
    }

    override fun onResume() {
        super.onResume()
        findAllHomes()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}