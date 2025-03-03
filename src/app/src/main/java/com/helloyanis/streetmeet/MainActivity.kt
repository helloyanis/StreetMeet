package com.helloyanis.streetmeet

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.aware.AttachCallback
import android.net.wifi.aware.DiscoverySession
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.PublishConfig
import android.net.wifi.aware.PublishDiscoverySession
import android.net.wifi.aware.SubscribeConfig
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.helloyanis.streetmeet.services.NotificationService
import com.helloyanis.streetmeet.ui.theme.StreetMeetTheme
import com.helloyanis.streetmeet.utils.DataStoreTalker
import com.helloyanis.streetmeet.view.AlertDialog

var wifiAwareDisabledDialogVisible by mutableStateOf(false)
var wifiAwareMissingPermission by mutableStateOf(false)
var wifiAwareIncompatible by mutableStateOf(false)
var wifiAwareAttachFailed by mutableStateOf(false)
var wifiAwareSubscribeStarted by mutableStateOf(false)
var wifiAwarePublishStarted by mutableStateOf(false)
var sendingNotification by mutableStateOf(false)
private var discoverySession: DiscoverySession? = null
var showMessagePopup by mutableStateOf(false)
var messageText by mutableStateOf("")
var nearbyDevicesAmount by mutableIntStateOf(0)
var backgroundUse by mutableStateOf(false)


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    private lateinit var appPermissionLauncher: ActivityResultLauncher<Array<String>>
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StreetMeetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    RootNavHost()
                    if (checkSelfPermission(android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        val appPermission = arrayOf(
                            android.Manifest.permission.POST_NOTIFICATIONS,
                            android.Manifest.permission.NEARBY_WIFI_DEVICES
                        )

                        val appPermissionLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestMultiplePermissions(),
                            onResult = { permissions ->
                                val permissionsGranted =
                                    permissions.values.reduce { acc, isPermissionGranted ->
                                        acc && isPermissionGranted
                                    }

                                if (permissionsGranted) {
                                    wifiAwareMissingPermission = false
                                    val intent = Intent(
                                        this,
                                        MainActivity::class.java
                                    )
                                    this.startActivity(intent)
                                    this.finishAffinity()
                                } else {
                                    wifiAwareMissingPermission = true

                                }
                            }
                        )

                        LaunchedEffect(Unit) {
                            appPermissionLauncher.launch(appPermission)
                        }
                    }

                    val notificationService = NotificationService(
                        this, getSystemService(
                            NotificationManager::class.java
                        )!!
                    )
                    notificationService.createChannelNotification()
                    CheckState(
                        notificationService = notificationService,
                        finishAndRemoveTask = { finishAndRemoveTask() },
                        context = LocalContext.current,
                        notificationTitle = "StreetMeet",
                        notificationContent = messageText
                    )
                    if (showMessagePopup) {
                        AlertDialog(
                            onDismissRequest = { showMessagePopup = false },
                            onConfirmation = {
                                showMessagePopup = false
                            },
                            dialogTitle = "Message reçu",
                            dialogText = messageText,
                            icon = Icons.Default.Info,
                            confirmationText = "OK"
                        )
                    }
                }
            }
        }
        print("before wifiAware")
        WifiAwareCallback(this).wifi()
        val hasSystemFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
        if (hasSystemFeature &&
            checkSelfPermission(android.Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
                //Log nearby devices
                val wifiAwareManager =
                    getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager
            val filter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)
            wifiAwareDisabledDialogVisible = !wifiAwareManager.isAvailable
            val myReceiver = object : BroadcastReceiver() {

                override fun onReceive(context: Context, intent: Intent) {
                    // Check if wifi has been enabled after initial check
                    wifiAwareDisabledDialogVisible = !wifiAwareManager.isAvailable
                }
            }
            this.registerReceiver(myReceiver, filter)

            wifiAwareManager.attach(object : AttachCallback() {
                    override fun onAttached(session: WifiAwareSession) {
                        super.onAttached(session)
                        val publishConfig = PublishConfig.Builder()
                            .setServiceName("com.helloyanis.streetmeet")
                            .build()

                        session.publish(publishConfig, object : DiscoverySessionCallback() {


                            override fun onPublishStarted(session: PublishDiscoverySession) {
                                super.onPublishStarted(session)
                                println("Publish started")
                                wifiAwarePublishStarted = true
                                discoverySession = session
                            }


                            override fun onServiceDiscovered(
                                peerHandle: PeerHandle,
                                serviceSpecificInfo: ByteArray?,
                                matchFilter: List<ByteArray>?
                            ) {
                                super.onServiceDiscovered(
                                    peerHandle,
                                    serviceSpecificInfo,
                                    matchFilter
                                )
                                nearbyDevicesAmount++
                                println("1 Service discovered from peer: $peerHandle")
                                Toast.makeText(
                                    applicationContext,
                                    "1",
                                    Toast.LENGTH_LONG
                                ).show()
                                // Envoyer le message en utilisant la variable de membre discoverySession
                                val message = DataStoreTalker(applicationContext).messageValue
                                discoverySession?.sendMessage(
                                    peerHandle,
                                    0,
                                    message.toByteArray()
                                )

                                println("1Message sent to peer: $peerHandle")

                            }
                            override fun onServiceLost(peerHandle: PeerHandle, reason: Int) {
                                nearbyDevicesAmount--
                                super.onServiceLost(peerHandle, reason)
                            }
                            override fun onMessageReceived(
                                peerHandle: PeerHandle,
                                message: ByteArray
                            ) {
                                super.onMessageReceived(peerHandle, message)

                                println("1Message received from peer: $peerHandle : ${String(message)}")
                                messageText = String(message)
                                showMessagePopup = true
                            }
                        }, null)

                        val subscribeConfig = SubscribeConfig.Builder()
                            .setServiceName("com.helloyanis.streetmeet")
                            .build()

                        session.subscribe(subscribeConfig, object : DiscoverySessionCallback() {
                            override fun onSubscribeStarted(session: SubscribeDiscoverySession) {
                                super.onSubscribeStarted(session)
                                println("Subscribe started")
                                wifiAwareSubscribeStarted = true
                                discoverySession = session
                            }

                            override fun onServiceDiscovered(
                                peerHandle: PeerHandle,
                                serviceSpecificInfo: ByteArray?,
                                matchFilter: List<ByteArray>?
                            ) {
                                super.onServiceDiscovered(
                                    peerHandle,
                                    serviceSpecificInfo,
                                    matchFilter
                                )
                                nearbyDevicesAmount++
                                println("Service discovered from peer: $peerHandle")
                                Toast.makeText(
                                    applicationContext,
                                    "Service discovered",
                                    Toast.LENGTH_LONG
                                ).show()
                                sendingNotification = true
                                val message = DataStoreTalker(applicationContext).messageValue
                                discoverySession?.sendMessage(
                                    peerHandle,
                                    0,
                                    message.toByteArray()
                                )
                                println("Message sent to peer: $peerHandle : $message")
                            }

                            override fun onServiceLost(peerHandle: PeerHandle, reason: Int) {
                                nearbyDevicesAmount--
                                super.onServiceLost(peerHandle, reason)
                            }
                            override fun onMessageReceived(
                                peerHandle: PeerHandle,
                                message: ByteArray
                            ) {
                                super.onMessageReceived(peerHandle, message)

                                println("Message received from peer: $peerHandle : ${String(message)}")
                                Toast.makeText(
                                    applicationContext,
                                    String(message),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }, null)

                        }

                override fun onAttachFailed() {
                    wifiAwareAttachFailed = true
                    unregisterReceiver(myReceiver)
                    super.onAttachFailed()
                }
                }, null)
        } else {
            if (!hasSystemFeature) {
                wifiAwareIncompatible = true
            } else {
                wifiAwareMissingPermission = true
            }
        }
        println("after wifi Aware")
    }
}