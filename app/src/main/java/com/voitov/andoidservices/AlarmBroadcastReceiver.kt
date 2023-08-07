package com.voitov.andoidservices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("ALARM_MANAGER", "onReceived")
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = getNotificationChannel()
            notificationManager.createNotificationChannel(channel)
        }
        val notificationItem = createNotification(context).build()
        notificationManager.notify(NOTIFICATION_ID, notificationItem)
    }

    private fun createNotification(context: Context) =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Alarm manager")
            .setContentText("The timer has gone")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOnlyAlertOnce(true)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel(): NotificationChannel {
        return NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
    }

    companion object {
        private const val CHANNEL_ID = "CHANNEL_ID_ALARM"
        private const val CHANNEL_NAME = "test channel"
        private const val NOTIFICATION_ID = 3
        private const val EXTRA_START = "start"
        private const val TAG = "SERVICE_TAG"

        @JvmStatic
        fun newIntent(context: Context) = Intent(context, AlarmBroadcastReceiver::class.java)
    }
}