package com.voitov.andoidservices.worker

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.text.SimpleDateFormat
import java.util.Date

class MyWorker2(
    context: Context,
    workerParameters: WorkerParameters,
) : Worker(context, workerParameters) {

    companion object {
        const val WORKER_NAME = "cool_worker"
        const val WORKER_OUTPUT_DATE = "output_date"

        fun newInstance(groceryId: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<MyWorker2>()
                .addTag(groceryId)
                .build()
        }
    }

    override fun doWork(): Result {

        val workBunch = mutableListOf<Int>()

        return try {
            repeat(10) { id ->
                Log.d("MyWorker2", "id: $id")
                Thread.sleep(1000)
                workBunch.add(id)
            }
            Result.success(
                workDataOf(
                    WORKER_OUTPUT_DATE to SimpleDateFormat("hh:mm").format(Date()).toString()
                )
            )
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
