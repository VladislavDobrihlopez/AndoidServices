package com.voitov.andoidservices

import android.content.Context
import android.util.Log
import androidx.work.*

class MyWorker(
    context: Context,
    private val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        log("doWork")
        return try {
            val page = workerParameters.inputData.getInt(EXTRA_PAGE, 0)
            for (seconds in 0 until 10) {
                Thread.sleep(1000)
                log("page=$page, seconds=$seconds")
            }
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }

    companion object {
        private const val TAG = "SERVICE_TAG"
        private const val EXTRA_PAGE = "extra_page"
        const val WORKER_NAME = "the best worker"

        fun makeRequest(page: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<MyWorker>()
                .setInputData(workDataOf(EXTRA_PAGE to page))
                .setConstraints(makeConstraints())
                .build()
        }

        private fun makeConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiresCharging(true)
                .build()
        }
    }
}