package com.voitov.andoidservices

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.voitov.andoidservices.databinding.ActivityMainBinding
import com.voitov.andoidservices.worker.MyWorker
import com.voitov.andoidservices.worker.MyWorker2
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private var page = 0
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MyForegroundService.ProgressCallbackBinder
            val currentService = binder?.getService() ?: return
            currentService.callback = { updatedProgress ->
                binding.progressBar.progress = updatedProgress
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    private val workManager by lazy {
        WorkManager.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonService.setOnClickListener {
            startService(MyService.newIntent(application, 19))
        }

        binding.buttonForegroundService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyForegroundService.newIntent(this, 0))
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
            Log.d("ALARM_MANAGER", "onTapped")

            val finalCountdown = Calendar.getInstance()
            finalCountdown.add(Calendar.SECOND, 21)
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = AlarmBroadcastReceiver.newIntent(this)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                100,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, finalCountdown.timeInMillis, pendingIntent)
        }

        binding.buttonWorkManager.setOnClickListener {
            workManager.enqueueUniqueWork(
                MyWorker.WORKER_NAME,
                ExistingWorkPolicy.APPEND,
                MyWorker.makeRequest(page++)
            )
        }

        binding.buttonTaggedWorkManager.setOnClickListener {
            var liveData = getLatestWorkInProgress()

            if (liveData == null) {
                val groceryId = "draniki"
                val workRequest = MyWorker2.newInstance(groceryId)

                workManager.enqueueUniqueWork(
                    MyWorker2.WORKER_NAME,
                    ExistingWorkPolicy.APPEND_OR_REPLACE,
                    workRequest,
                )

                liveData = workManager.getWorkInfoByIdLiveData(workRequest.id)

                liveData.observe(this) { workInfo ->
                    Toast.makeText(this, "${workInfo.state}", Toast.LENGTH_SHORT).show()

                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val currentDate = workInfo.outputData.getString(MyWorker2.WORKER_OUTPUT_DATE)
                        Toast.makeText(this, "$currentDate", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun getLatestWorkInProgress(): LiveData<WorkInfo>? {
        val workersInfo = workManager.getWorkInfosByTag("draniki").get()

        for (workerInfo in workersInfo) {
            if (!workerInfo.state.isFinished) return workManager.getWorkInfoByIdLiveData(workerInfo.id)
        }

        return null
    }


    override fun onStart() {
        super.onStart()
        bindService(MyForegroundService.newIntent(this, 0), serviceConnection, 0)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
}