package com.openclassrooms.realestatemanager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.EditAddActivity


class NotificationWorker(private val context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters){

    companion object {

        /**
         * Plan a OneTime Notification with Property's info
         */
        @JvmStatic
        fun configureNotification(data: Data){
            val notification = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setInputData(data)
                    .build()
            val instance = WorkManager.getInstance()
            instance.enqueue(notification)
        }
    }


    override fun doWork(): Result {
        //-- Get user's name from data --
        val name = inputData.getString(Constants.DATA_USER_NAME)
        sendNotification(name!!)
        return Result.success()
    }

    private fun sendNotification(name: String){
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, EditAddActivity::class.java), PendingIntent.FLAG_ONE_SHOT)
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "channelId"
        val text = context.getString(R.string.notification_txt, name)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle(context.getString(R.string.notification_title))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.notification_channel)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notificationBuilder.build())
    }
}