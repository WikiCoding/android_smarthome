package com.wikicoding.androidsmarthome.activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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

    fun showRationalDialogForPermissions(context: Context) {
        AlertDialog.Builder(context).setMessage("Permissions denied for this app")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted
                Toast.makeText(this, "Location access granted", Toast.LENGTH_SHORT).show()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                Toast.makeText(this, "Only approximate location access granted", Toast.LENGTH_SHORT).show()
            }

            else -> {
                // No location access granted
                Toast.makeText(this, "No location access granted", Toast.LENGTH_SHORT).show()
                showRationalDialogForPermissions(this)
            }
        }
    }

    fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            // Before you perform the actual permission request, check whether your app
            // already has the permissions, and whether your app needs to show a permission
            // rationale dialog. For more details, see Request permissions.
            return locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}