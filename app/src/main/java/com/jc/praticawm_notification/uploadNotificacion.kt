package com.jc.praticawm_notification

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

class uploadNotificacion(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    override fun doWork(): Result {

        try {
            Log.i("[PASO]", "6 OK")

            val miMsg = inputData.getString("msg")?:"..."

            var builder = NotificationCompat.Builder(applicationContext, "com.jc.praticawm_notification")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Mini App Android")
                .setContentText(miMsg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setCategory(NotificationCompat.CATEGORY_CALL)

            Log.i("[PASO]", "5 OK")

            with(NotificationManagerCompat.from(applicationContext)) {
                // notificationId is a unique int for each notification that you must define
                Log.i("[PASO]", "5 OK 123456")
                notify(1, builder.build())
            }

            Log.i("[PASO]", "6 OK")
            return Result.success()

        }catch (ex: Exception) {
            Log.i("[PASO]", "7 ERROR")
            return Result.failure()
        }
    }
}