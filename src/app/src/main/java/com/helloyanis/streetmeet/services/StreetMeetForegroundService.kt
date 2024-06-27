package com.helloyanis.streetmeet.services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class StreetMeetForegroundService : Service() {
    private lateinit var notificationService: NotificationService
    private val notificationId = 1 // Utilisez un ID unique pour la notification

    companion object {
        const val EXTRA_DURATION_MS = "EXTRA_DURATION_MS"
    }

    override fun onCreate() {
        super.onCreate()
        notificationService = NotificationService(
            this,
            getSystemService(NotificationManager::class.java)!!
        )

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

        val durationMs = intent?.getLongExtra(EXTRA_DURATION_MS, 0L) ?: 0L
        if (durationMs > 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                stopForeground(true)
                stopSelf()
            }, durationMs)
        }


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