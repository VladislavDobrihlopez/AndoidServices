package com.voitov.andoidservices

import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.voitov.andoidservices.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var page = 0
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonService.setOnClickListener {
            startService(MyService.newIntent(application, 19))
        }

        binding.buttonForegroundService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyForegroundService.newIntent(this, 30))
        }

        binding.buttonStopForegroundService.setOnClickListener {
            stopService(MyForegroundService.newIntent(this))
        }

        binding.buttonIntentService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyIntentService.newIntent(this, 10))
        }

        binding.buttonJobScheduler.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val jobInfo = MyJobService.newJobInfo(this)
                val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
                jobScheduler.enqueue(jobInfo, JobWorkItem(MyJobService.newIntent(this, page++)))
            } else {
                startService(MyIntentService2.newIntent(this, page++))
            }
        }

        binding.buttonJobIntentService.setOnClickListener {
            MyJobIntentService.enqueue(this, 0)
        }

        binding.buttonAlarmManager.setOnClickListener {
            Toast.makeText(this, "Is gonna to be implemented", Toast.LENGTH_SHORT).show()
        }

        binding.buttonWorkManager.setOnClickListener {
            val workManager = WorkManager.getInstance(application)
            workManager.enqueueUniqueWork(
                MyWorker.WORKER_NAME,
                ExistingWorkPolicy.APPEND,
                MyWorker.makeRequest(page++)
            )
        }
    }
}