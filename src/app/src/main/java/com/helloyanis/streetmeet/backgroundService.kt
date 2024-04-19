package com.helloyanis.streetmeet

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat


class BackgroundService : Service() {

    private val NOTIF_ID = 1
    private val NOTIF_CHANNEL_ID = "Channel_Id"
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, getText(R.string.service_begin), Toast.LENGTH_SHORT).show()


        startForeground()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Toast.makeText(this, getText(R.string.service_end), Toast.LENGTH_SHORT).show()
    }


    private fun startForeground(){
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        startForeground(NOTIF_ID, NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(androidx.core.R.drawable.ic_call_answer)
            .setContentTitle("StreetMeet")
            .setContentText("scan service is running background")
            .setContentIntent(pendingIntent)
            .build())
    }
}