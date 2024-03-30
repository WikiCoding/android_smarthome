package com.wikicoding.androidsmarthome.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.wikicoding.androidsmarthome.R
import com.wikicoding.androidsmarthome.constants.Constants
import com.wikicoding.androidsmarthome.databinding.ActivityAddBinding
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.RoomEntity
import com.wikicoding.androidsmarthome.utils.GetAddressFromLatLng
import kotlinx.coroutines.launch

class AddActivity : BaseActivity() {
    private var binding: ActivityAddBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var pleaseWaitDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        pleaseWaitDialog = Dialog(this)
        pleaseWaitDialog.setContentView(R.layout.dialog_custom_progress)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // fetch upfront location, long running task
        getLocation()

        if (intent.hasExtra(Constants.intentHomeEditExtra)) {
            handleEditHome()
        }

        if (intent.hasExtra(Constants.intentHomeExtra)) {
            handleCreateHome()
        }
    }

    private fun handleCreateHome() {
        supportActionBar!!.title = "Add ${Constants.intentHomeValueExtra}"

        binding!!.btnLocate.setOnClickListener {
            if (isLocationEnabled()) {
                pleaseWaitDialog.show()
                getLocation()
            } else {
                Toast.makeText(this, "Please turn on Location", Toast.LENGTH_LONG).show()
            }
        }

        binding!!.btnAdd.setOnClickListener {
            val homeName = binding!!.etHomeName.text.toString()
            val homeAddress = binding!!.etAddress.text.toString()

            if (homeName.isEmpty() || homeAddress.isBlank() ||
                homeAddress.isEmpty() || homeAddress.isBlank()
            ) {
                dialogErrorFillingForm(this)
                return@setOnClickListener
            }

            val home = HomeEntity(0, homeName.trim(), homeAddress.trim(), latitude, longitude)
            addHome(home)
            finish()
        }
    }

    private fun handleEditHome() {
        val editingHome =
            intent.getSerializableExtra(Constants.intentHomeEditExtra) as HomeEntity?
        supportActionBar!!.title = "Edit Home ${editingHome!!.homeName}"

        binding!!.etHomeName.setText(editingHome.homeName)
        binding!!.etAddress.setText(editingHome.address)
        binding!!.etLatitude.setText(editingHome.lat.toString())
        binding!!.etLongitude.setText(editingHome.lng.toString())

        binding!!.btnAdd.text = "Update Home"
        binding!!.btnLocate.setOnClickListener {
            if (isLocationEnabled()) {
                pleaseWaitDialog.show()
                getLocation()
            } else {
                Toast.makeText(this, "Please turn on Location", Toast.LENGTH_LONG).show()
            }
        }

        binding!!.btnAdd.setOnClickListener {
            val homeName = binding!!.etHomeName.text.toString()
            val homeAddress = binding!!.etAddress.text.toString()

            if (homeName.isEmpty() || homeAddress.isBlank() ||
                homeAddress.isEmpty() || homeAddress.isBlank()
            ) {
                dialogErrorFillingForm(this)
                return@setOnClickListener
            }

            val home = HomeEntity(
                editingHome!!.homeId, homeName.trim(), homeAddress.trim(),
                latitude, longitude
            )
            updateHome(home)
            finish()
        }
    }

    private fun getLocation() {
        val priority = 100
        val interval: Long = 1000
        val maxUpdates = 1
        requestLocationData(fusedLocationClient, priority, interval, maxUpdates)
    }

    private fun updateHome(home: HomeEntity) {
        lifecycleScope.launch {
            dao.updateHome(home)
        }
    }

    private fun addHome(homeEntity: HomeEntity) {
        lifecycleScope.launch {
            dao.addHome(homeEntity)
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

    @SuppressLint("MissingPermission")
    private fun requestLocationData(fusedLocationClient: FusedLocationProviderClient, priority: Int,
                                    intervalMillis: Long, maxUpdates: Int) {
        val locationRequest = LocationRequest.Builder(priority, intervalMillis).setMaxUpdates(maxUpdates).build()

        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            latitude = mLastLocation?.latitude!!
            longitude = mLastLocation.longitude

            requestLocation(latitude, longitude)
        }
    }

    private fun requestLocation(latitude: Double, longitude: Double) {
        val address = GetAddressFromLatLng(this, latitude, longitude)

        address.setAddressListener(object : GetAddressFromLatLng.AddressListener {
            override fun onAddressFound(address: String?) {
                binding!!.etAddress.setText(address.toString())
                binding!!.etLatitude.setText(latitude.toString())
                binding!!.etLongitude.setText(longitude.toString())
                pleaseWaitDialog.dismiss()
            }

            override fun onError() {
                Toast.makeText(this@AddActivity, "Location error", Toast.LENGTH_SHORT).show()
            }
        })

        address.getAddress()
    }

    private fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}