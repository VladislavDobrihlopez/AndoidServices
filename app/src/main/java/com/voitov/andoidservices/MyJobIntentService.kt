package com.voitov.andoidservices

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService

class MyJobIntentService : JobIntentService() {
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onHandleWork(intent: Intent) {
        log("onHandleWork")
        val page = intent.getIntExtra(EXTRA_START, 0)
        for (seconds in 0 until 5) {
            Thread.sleep(1000)
            log("page=$page, seconds=$seconds")
        }
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
        private const val JOB_ID = 1

        fun enqueue(context: Context, startPage: Int = 0) {
            val intent = newIntent(context, startPage)
            enqueueWork(context, MyJobIntentService::class.java, JOB_ID, intent)
        }

        private fun newIntent(context: Context, startPage: Int): Intent {
            return Intent(context, MyJobIntentService::class.java).apply {
                putExtra(EXTRA_START, startPage)
            }
        }
    }
}