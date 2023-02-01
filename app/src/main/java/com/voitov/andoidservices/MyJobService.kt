package com.voitov.andoidservices

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*

class MyJobService : JobService() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartJob")
        coroutineScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    val page = workItem.intent?.extras?.getInt(EXTRA_PAGE) ?: 0
                    for (seconds in 0 until 5) {
                        delay(1000)
                        log("page=$page, seconds=$seconds")
                    }
                    params?.completeWork(workItem)
                    workItem = params?.dequeueWork()
                }
                jobFinished(params, true)
            }
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }

    companion object {
        private const val TAG = "SERVICE_TAG"
        private const val EXTRA_PAGE = "EXTRA_PAGE"
        private const val JOB_ID = 1

        fun newJobInfo(pckg: Context): JobInfo {
            val componentName = ComponentName(pckg, MyJobService::class.java)
            return JobInfo.Builder(JOB_ID, componentName)
                .build()
        }

//        fun newBundle(page: Int): PersistableBundle {
//            return PersistableBundle().apply {
//                putInt(PAGE_EXTRA, page)
//            }
//        }

        fun newIntent(context: Context, page: Int): Intent {
            return Intent().apply {
                putExtra(EXTRA_PAGE, page)
            }
        }
    }
}