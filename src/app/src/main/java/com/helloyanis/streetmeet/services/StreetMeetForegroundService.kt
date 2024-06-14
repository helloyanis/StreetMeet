package com.helloyanis.streetmeet.services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder

class StreetMeetForegroundService : Service() {
    private lateinit var notificationService: NotificationService
    private val notificationId = 1 // Utilisez un ID unique pour la notification


    override fun onCreate() {
        super.onCreate()
        notificationService = NotificationService(this, getSystemService(NotificationManager::class.java)!!)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action == "STOP") {
            stopForeground(true) // Arrête le service au premier plan et supprime la notification
            stopSelf()
            return START_NOT_STICKY
        }

        // Créez ou mettez à jour la notification
        notificationService.createChannelNotification()
        val notification = notificationService.send("StreetMeet", "StreetMeet is running", notificationId)
        startForeground(notificationId, notification.second)
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