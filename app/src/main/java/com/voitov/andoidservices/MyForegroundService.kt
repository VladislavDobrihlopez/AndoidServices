package com.voitov.andoidservices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyForegroundService : Service() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = setupNotification().build()
        startForeground(NOTIFICATION_ID, notification)
        log("onCreate")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun setupNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("title da dad a")
            .setContentText("text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOnlyAlertOnce(true)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        var start = intent?.getIntExtra(EXTRA_START, 0) ?: 0
        coroutineScope.launch {
            for (progress in start until 101) {
                delay(1000)
                log((++start).toString())
                val updatedNotify = setupNotification()
                    .setProgress(100, progress, false)
                    .build()
                notificationManager.notify(NOTIFICATION_ID, updatedNotify)
            }
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }

    companion object {
        private const val CHANNEL_ID = "some id"
        private const val CHANNEL_NAME = "test channel"
        private const val NOTIFICATION_ID = 2
        private const val EXTRA_START = "start"
        private const val TAG = "SERVICE_TAG"

        fun newIntent(context: Context, start: Int = 0): Intent {
            return Intent(context, MyForegroundService::class.java).apply {
                putExtra(EXTRA_START, start)
            }
        }
    }
}