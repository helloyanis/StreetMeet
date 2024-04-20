package com.helloyanis.streetmeet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class NotificationService(
    private val context: Context,
    private val name: String
) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannelNotification() {
        val notificationChannel = NotificationChannel(
            "alertMeet",
            "Meet people",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)
        println("[INFO] Channel created")
    }

    fun showBasicNotification() {
        val notification = NotificationCompat.Builder(context, "alertMeet")
            .setContentTitle("Vous avez croisé quelqu'un")
            .setContentText("$name est a proximité, envoie lui un message")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }
}