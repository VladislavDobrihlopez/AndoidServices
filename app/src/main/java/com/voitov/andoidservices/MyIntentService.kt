package com.voitov.andoidservices

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MyIntentService : IntentService(NAME) {
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        setIntentRedelivery(true)
        createNotificationChannel()
        val notification = setupNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onHandleIntent(intent: Intent?) {
        log("onHandleIntent")
        var start = intent?.getIntExtra(EXTRA_START, 0) ?: 0
        for (seconds in start until start + 100) {
            Thread.sleep(1000)
            log((++start).toString())
        }
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun setupNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("title da dad a")
            .setContentText("text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }

    companion object {
        private const val CHANNEL_ID = "some id"
        private const val CHANNEL_NAME = "test channel"
        private const val NOTIFICATION_ID = 1
        private const val EXTRA_START = "start"
        private const val TAG = "MyService"
        private const val NAME = "MyIntentService"

        fun newIntent(context: Context, start: Int = 0): Intent {
            return Intent(context, MyIntentService::class.java).apply {
                putExtra(EXTRA_START, start)
            }
        }
    }
}