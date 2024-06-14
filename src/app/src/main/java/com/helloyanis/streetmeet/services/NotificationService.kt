package com.helloyanis.streetmeet.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.helloyanis.streetmeet.R

class NotificationService(
    private val context: Context,
    private val notificationManager: NotificationManager
) {
    fun createChannelNotification() {
        val notificationChannel = NotificationChannel(
            "alertmeet",
            "Meet people",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)
        println("[INFO] Channel created")
    }

    fun send(title: String, content: String, notificationId: Int): Pair<Int, Notification> {
        val builder = NotificationCompat.Builder(context, "alertmeet")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.wifi_tethering)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notification = builder.build()
        notificationManager.notify(notificationId, notification)
        return notificationId to notification
    }
}