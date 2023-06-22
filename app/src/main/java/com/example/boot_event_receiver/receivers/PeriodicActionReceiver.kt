package com.example.boot_event_receiver.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.boot_event_receiver.DatabaseHelper
import com.example.boot_event_receiver.R

class PeriodicActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val channelId = "boot_events_channel"
        val notificationId = 1
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val databaseHelper = DatabaseHelper(context)

        val bootEvents = databaseHelper.getAllBootEvents()

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)

        if (bootEvents.isEmpty()) {
            notificationBuilder.setContentTitle(context.getString(R.string.no_boot_detected))
                .setContentText(context.getString(R.string.no_boot_detected))
        } else if (bootEvents.size == 1) {
            val timestamp = bootEvents.first().timestamp
            notificationBuilder.setContentTitle(context.getString(R.string.the_boot_detected))
                //TODO Should be replaced to resources string
                .setContentText(context.getString(R.string.the_boot_detected_was) + " " + timestamp)
        } else {
            val lastBootTimestamp = bootEvents.last().timestamp
            val preLastBootTimestamp = bootEvents[bootEvents.size - 2].timestamp
            val timeDelta = lastBootTimestamp - preLastBootTimestamp
            //TODO Should be replaced to resources string
            notificationBuilder.setContentTitle(context.getString(R.string.last_boots_time))
                .setContentText(context.getString(R.string.last_boots_time) + " " + timeDelta)
        }
        databaseHelper.close()
        val notification = notificationBuilder.build()
        notificationManager.notify(notificationId, notification)
    }
}