package com.helloyanis.streetmeet

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import kotlin.random.Random
class NotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun createChannelNotification() {
        val notificationChannel = NotificationChannel(
            "alertMeet",
            "Meet people",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)
        println("[INFO] Channel created")
    }

    fun showBasicNotification(title: String, content: String): Pair<Int, Notification> {
        val notification = NotificationCompat.Builder(context, "alertMeet")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationId = Random.nextInt()
        notificationManager.notify(notificationId, notification)
        return Pair(notificationId, notification)
    }
}
