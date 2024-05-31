package com.helloyanis.streetmeet

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class StreetMeetForegroundService : Service() {
    private lateinit var notificationService: NotificationService

    override fun onCreate() {
        super.onCreate()
        notificationService = NotificationService(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationService.createChannelNotification()
        val notification = notificationService.showBasicNotification("StreetMeet", "StreetMeet is running")
        startForeground(notification.first, notification.second)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }
}