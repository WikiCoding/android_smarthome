package com.wikicoding.androidsmarthome.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.wikicoding.androidsmarthome.R
import com.wikicoding.androidsmarthome.adapter.DevicesAdapter
import com.wikicoding.androidsmarthome.adapter.MainAdapter
import com.wikicoding.androidsmarthome.adapter.RoomsAdapter
import com.wikicoding.androidsmarthome.dao.SmartHomeApp
import com.wikicoding.androidsmarthome.dao.SmartHomeDao
import com.wikicoding.androidsmarthome.databinding.DeleteConfirmationDialogBinding
import com.wikicoding.androidsmarthome.model.DeviceEntity
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.RoomEntity
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {
    lateinit var dao: SmartHomeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = (application as SmartHomeApp).db.smartHomeDao()
    }

    fun dialogErrorFillingForm(context: Context) {
        val formErrorDialog = AlertDialog.Builder(context)
        formErrorDialog.setIcon(R.drawable.baseline_warning_24)
        formErrorDialog.setTitle("Error")
        formErrorDialog.setMessage("Fields cannot be empty")
        formErrorDialog.setPositiveButton("OK") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
//        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        formErrorDialog.show()
    }

    fun deleteConfirmationDialog(context: Context, homeEntity: HomeEntity?, roomEntity: RoomEntity?, deviceEntity: DeviceEntity?,
                                 homeList: ArrayList<HomeEntity>?, roomsList: ArrayList<RoomEntity>?, deviceList: ArrayList<DeviceEntity>?,
                                 homeAdapter: MainAdapter?, roomsAdapter: RoomsAdapter?, devicesAdapter: DevicesAdapter?,
                                 position: Int) {
        val (deleteConfirmationDialog, dialogBinding) = createShowDeleteDialog(context)

        handleDeleteDialogProceedClick(dialogBinding, context, homeEntity, roomEntity, deviceEntity,
            homeList, roomsList, deviceList, homeAdapter, roomsAdapter, devicesAdapter, position, deleteConfirmationDialog)

        handleDeleteDialogCancelClick(dialogBinding, deleteConfirmationDialog, context, homeAdapter, roomsAdapter, devicesAdapter)
    }

    private fun createShowDeleteDialog(context: Context): Pair<Dialog, DeleteConfirmationDialogBinding> {
        val deleteConfirmationDialog = Dialog(context, R.style.Theme_AndroidSmartHome)
        //avoiding that clicking outside will not close the dialog or update data
        deleteConfirmationDialog.setCancelable(false)
        val dialogBinding = DeleteConfirmationDialogBinding.inflate(layoutInflater)
        deleteConfirmationDialog.setContentView(dialogBinding.root)
        deleteConfirmationDialog.show()
        return Pair(deleteConfirmationDialog, dialogBinding)
    }

    private fun handleDeleteDialogCancelClick(
        dialogBinding: DeleteConfirmationDialogBinding,
        deleteConfirmationDialog: Dialog,
        context: Context,
        homeAdapter: MainAdapter?,
        roomAdapter: RoomsAdapter?,
        deviceAdapter: DevicesAdapter?
    ) {
        dialogBinding.tvCancel.setOnClickListener {
            deleteConfirmationDialog.dismiss()
            if (context is MainActivity) {
                homeAdapter!!.notifyDataSetChanged()
            } else if (context is RoomActivity) {
                roomAdapter!!.notifyDataSetChanged()
            } else {
                deviceAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun handleDeleteDialogProceedClick(dialogBinding: DeleteConfirmationDialogBinding,
                                               context: Context, home: HomeEntity?, room: RoomEntity?, device: DeviceEntity?,
                                               homeList: ArrayList<HomeEntity>?, roomList: ArrayList<RoomEntity>?, deviceList: ArrayList<DeviceEntity>?,
                                               homeAdapter: MainAdapter?, roomAdapter: RoomsAdapter?, devicesAdapter: DevicesAdapter?,
                                               position: Int, deleteConfirmationDialog: Dialog
    ) {
        dialogBinding.tvProceed.setOnClickListener {
            if (home != null) {
                proceedDeleting(home, null, null, homeList!!, null, null, homeAdapter!!,null, null, position)
            } else if (room != null) {
                proceedDeleting(null, room, null, null, roomList!!, null, null, roomAdapter!!, null, position)
            } else if (device != null) {
                proceedDeleting(null, null, device, null, null, deviceList, null, null, devicesAdapter, position)
            }
            deleteConfirmationDialog.dismiss()
        }
    }

    private fun proceedDeleting(home: HomeEntity?, room: RoomEntity?, device: DeviceEntity?,
                                homeList: ArrayList<HomeEntity>?, roomList: ArrayList<RoomEntity>?, deviceList: ArrayList<DeviceEntity>?,
                                homeAdapter: MainAdapter?, roomAdapter: RoomsAdapter?, deviceAdapter: DevicesAdapter?, position: Int) {
        if (home != null) {
            lifecycleScope.launch {
                dao.deleteHome(home!!)
                homeList!!.remove(home)
                homeAdapter!!.notifyItemRemoved(position)
            }
        } else if (room != null) {
            lifecycleScope.launch {
                dao.deleteRoom(room)
                roomList!!.remove(room)
                roomAdapter!!.notifyItemRemoved(position)
            }
        } else if (device != null) {
            lifecycleScope.launch {
                dao.deleteDevice(device)
                deviceList!!.remove(device)
                deviceAdapter!!.notifyItemRemoved(position)
            }
        }
    }
}