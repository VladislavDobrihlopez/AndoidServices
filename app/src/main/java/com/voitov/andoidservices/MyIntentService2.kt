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

class MyIntentService2 : IntentService(NAME) {
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        setIntentRedelivery(true)
    }

    override fun onHandleIntent(intent: Intent?) {
        log("onHandleIntent")
        var start = intent?.getIntExtra(EXTRA_START, 0) ?: 0
        for (seconds in start until start + 10) {
            Thread.sleep(1000)
            log((++start).toString())
        }
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
        private const val EXTRA_START = "start"
        private const val TAG = "SERVICE_TAG"
        private const val NAME = "MyIntentService"

        fun newIntent(context: Context, start: Int = 0): Intent {
            return Intent(context, MyIntentService2::class.java).apply {
                putExtra(EXTRA_START, start)
            }
        }
    }
}