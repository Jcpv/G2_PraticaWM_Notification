package com.jc.praticawm_notification

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

//Necesitamos implementar el metodo
class UploadWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams)  {
    override fun doWork(): Result {
        try {
            Log.i("[PASO]", "5 OK")
            val miVar = inputData.getInt("key1", 0)
            //.build()

            for (i:Int in 0..600) {
                Log.i("WorkerManagerTask", "Subiendo elemento " + i.toString())
            }
            Log.i("[PASO]", "6 OK")
            return Result.success()
        }catch (ex:Exception) {
            Log.i("[PASO]", "7 OK")
            return Result.failure()
        }
    }
}