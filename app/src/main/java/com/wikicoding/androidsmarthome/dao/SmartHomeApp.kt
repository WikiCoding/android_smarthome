package com.wikicoding.androidsmarthome.dao

import android.app.Application
import com.wikicoding.androidsmarthome.db.SmartHomeDb

class SmartHomeApp: Application() {
    val db by lazy {
        SmartHomeDb.getInstance(this)
    }
}