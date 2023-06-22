package com.example.boot_event_receiver.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.boot_event_receiver.DatabaseHelper

class BootEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val timestamp = System.currentTimeMillis()
            //TODO Should be added domain layer
            val databaseHelper = DatabaseHelper(context)

            val eventCount = databaseHelper.getAllBootEvents().size

            if (eventCount == 0) {
                databaseHelper.insertBootEvent(timestamp)
            } else {
                val lastBootEvent = databaseHelper.getAllBootEvents().last()
                val lastBootTimestamp = lastBootEvent.timestamp
                val timeDelta = timestamp - lastBootTimestamp
                databaseHelper.insertBootEvent(timestamp)
            }

            databaseHelper.close()

            schedulePeriodicAction(context)
        }
    }
    private fun schedulePeriodicAction(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PeriodicActionReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val intervalMillis = 15 * 60 * 1000 // 15 minutes
        val triggerAtMillis = System.currentTimeMillis() + intervalMillis
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }
    // Rest of the code remains the same
}