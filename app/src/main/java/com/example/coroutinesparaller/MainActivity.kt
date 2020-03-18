package com.example.coroutinesparaller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private var MAX_PROGRESS = 100
    private var MIN_PROGRESS = 0
    private var TIME = 4000
    lateinit var job: CompletableJob
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            if (!::job.isInitialized) {

                initJob()
            }
            progressBar.startOrCancellJob(job)
        }
    }


    fun ProgressBar.startOrCancellJob(job: Job) {

        if (this.progress > 0) {

            println("job has been cancelles")
            resetJob(job)
        } else {
            btn.setText("Cancel job #1")
            CoroutineScope(IO + job).launch {

               var job1 =  launch {
                    for (i in MIN_PROGRESS..MAX_PROGRESS) {
                        delay((TIME / MAX_PROGRESS).toLong())
                        progress = i
                    }
                }
               var job2 =   launch {

                    while (job.isActive)
                    {
                        println("${progress}")
                        setTextOnMainThread("${(progress)} %")

                    }
                }

                //job1 and job2 work in paraller

            }
        }

    }

    private suspend fun setTextOnMainThread(s: String) {
        //change thread from IO to Main
        withContext(Main) {
            tv_counter.text = s
        }

    }

    //reset job in order to reset all jobs in coroutines scope
    private fun resetJob(job: Job) {

        if(job.isCompleted || job.isActive){

            job.cancel(CancellationException("job has been cancelled"))

        }
        initJob()
    }


    private fun initJob() {

        job = Job()
        progressBar.max = MAX_PROGRESS
        progressBar.progress = 0

        tv_counter.text = "0%"
        btn.setText("click Me!")

        //when job finished or cancelled this method will be called
        job?.invokeOnCompletion {

            it?.let {
                var msg = it.message
                if (msg.isNullOrBlank()) {
                    msg = "cancell job with no reason"
                }

                showToast(msg)

            }
        }

    }

    private fun showToast(message: String) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)

        }
    }
}
