package com.helloyanis.streetmeet.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.aware.*
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.helloyanis.streetmeet.R

class StreetMeetForegroundService : Service() {
    private var wifiAwareManager: WifiAwareManager? = null
    private var session: WifiAwareSession? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var notificationService: NotificationService
    private val notificationId = 1 // Utilisez un ID unique pour la notification

    companion object {
        const val EXTRA_DURATION_MS = "EXTRA_DURATION_MS"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "StreetMeetServiceChannel",
                "StreetMeet Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(channel)
        }
        val notification = Notification.Builder(this, "StreetMeetServiceChannel")
            .setContentTitle("StreetMeet Service")
            .setContentText("Running in the background")
            .setSmallIcon(R.drawable.wifi_tethering)
            .build()
        startForeground(1, notification)

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StreetMeet::WakelockTag")
        wakeLock?.acquire()

        wifiAwareManager = getSystemService(WIFI_AWARE_SERVICE) as WifiAwareManager
        wifiAwareManager!!.attach(object : AttachCallback() {
            override fun onAttached(session: WifiAwareSession?) {
                this@StreetMeetForegroundService.session = session
                Toast.makeText(applicationContext, "Wi-Fi Aware Attached", Toast.LENGTH_SHORT).show()
                subscribe()
            }

            override fun onAttachFailed() {
                Toast.makeText(applicationContext, "Wi-Fi Aware Attach Failed", Toast.LENGTH_SHORT).show()
            }
        }, null)
    }

    private fun subscribe() {
        val subscribeConfig = SubscribeConfig.Builder()
            .setServiceName("StreetMeet")
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        session?.subscribe(subscribeConfig, object : DiscoverySessionCallback() {
            override fun onMessageReceived(peerHandle: PeerHandle?, message: ByteArray?) {
                val messageString = String(message!!)
                Toast.makeText(applicationContext, "Message received: $messageString", Toast.LENGTH_SHORT).show()
            }
        }, Handler(Looper.getMainLooper()))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle start command
        return START_STICKY
    }

    override fun onDestroy() {
        wakeLock?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
