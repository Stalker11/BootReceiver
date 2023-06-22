package com.example.boot_event_receiver

import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView

class MainActivity : AppCompatActivity() {
    private lateinit var bootEventTextView: AppCompatTextView
    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        //TODO Must be checked for Android 13
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bootEventTextView = findViewById(R.id.bootEventTextView)
        requestNotificationsPermission()
        // Retrieve boot event information from the SQLite database
        //TODO Should be added domain layer for works with DB and API
        val databaseHelper = DatabaseHelper(this)
        val bootEvents = databaseHelper.getAllBootEvents()
        databaseHelper.close()

        if (bootEvents.isEmpty()) {
            bootEventTextView.text = "No boots detected"
        } else {
            val stringBuilder = StringBuilder()

            for ((index, bootEvent) in bootEvents.withIndex()) {
                stringBuilder.append("${index + 1} - ${bootEvent.timestamp}\n")
            }

            bootEventTextView.text = stringBuilder.toString()
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationsPermission(){
        pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }
}
