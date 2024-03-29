package com.wikicoding.androidsmarthome.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.wikicoding.androidsmarthome.constants.Constants
import com.wikicoding.androidsmarthome.databinding.ActivityAddBinding
import com.wikicoding.androidsmarthome.model.HomeEntity
import com.wikicoding.androidsmarthome.model.RoomEntity
import kotlinx.coroutines.launch

class AddActivity : BaseActivity() {
    private var binding: ActivityAddBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(Constants.intentHomeEditExtra)) {
            val editingHome = intent.getSerializableExtra(Constants.intentHomeEditExtra) as HomeEntity?
            supportActionBar!!.title = "Edit Home ${editingHome!!.homeName}"

            binding!!.etHomeName.setText(editingHome?.homeName)
            binding!!.etAddress.setText(editingHome?.address)

            binding!!.btnAdd.text = "Update Home"

            binding!!.btnAdd.setOnClickListener {
                val homeName = binding!!.etHomeName.text.toString()
                val homeAddress = binding!!.etAddress.text.toString()

                if (homeName.isEmpty() || homeAddress.isBlank() ||
                    homeAddress.isEmpty() || homeAddress.isBlank()
                ) {
                    dialogErrorFillingForm(this)
                    return@setOnClickListener
                }

                val home = HomeEntity(editingHome!!.homeId, homeName.trim(), homeAddress.trim())
                updateHome(home)
                finish()
            }
        }

        if (intent.hasExtra(Constants.intentHomeExtra)) {
            supportActionBar!!.title = "Add ${Constants.intentHomeValueExtra}"

            binding!!.btnAdd.setOnClickListener {
                val homeName = binding!!.etHomeName.text.toString()
                val homeAddress = binding!!.etAddress.text.toString()

                if (homeName.isEmpty() || homeAddress.isBlank() ||
                    homeAddress.isEmpty() || homeAddress.isBlank()
                ) {
                    dialogErrorFillingForm(this)
                    return@setOnClickListener
                }

                val home = HomeEntity(0, homeName.trim(), homeAddress.trim())
                addHome(home)
                finish()
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}