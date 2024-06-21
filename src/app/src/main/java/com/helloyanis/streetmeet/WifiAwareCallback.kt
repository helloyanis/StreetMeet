package com.helloyanis.streetmeet

import android.Manifest
import android.content.Context
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
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WifiAwareCallback(val context: Context) {
    private var discoverySession: DiscoverySession? = null
    private var showMessagePopup by mutableStateOf(false)
    private var messageText by mutableStateOf("")
    private var sendingNotification by mutableStateOf(false)
    private var wifiAwareDisabledDialogVisible by mutableStateOf(false)
    private var wifiAwareScanFailed by mutableStateOf(false)
    private var wifiAwareIncompatible by mutableStateOf(false)

    fun wifi(){
        print("in wifi aware")
        val hasSystemFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
        if (hasSystemFeature && context.checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            //Log nearby devices
            val wifiAwareManager =
                context.getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager
            if (!wifiAwareManager.isAvailable) {
                wifiAwareDisabledDialogVisible = true
            }
            wifiAwareManager.attach(object : AttachCallback() {
                override fun onAttached(session: WifiAwareSession) {
                    super.onAttached(session)
                    val publishConfig = PublishConfig.Builder()
                        .setServiceName("com.helloyanis.streetmeet")
                        .build()

                    if (context.checkSelfPermission(

                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(
                            Manifest.permission.NEARBY_WIFI_DEVICES
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        println("no permission")
                        return
                    }
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
                                context,
                                "1",
                                Toast.LENGTH_LONG
                            ).show()
                            // Envoyer le message en utilisant la variable de membre discoverySession
                            discoverySession?.sendMessage(
                                peerHandle,
                                0,
                                "Hello".toByteArray()
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
                                context,
                                "Service discovered",
                                Toast.LENGTH_LONG
                            ).show()
                            sendingNotification = true
                            val message = "Hi there"
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
                                context,
                                String(message),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }, null)

                }
            }, null)
        } else {
            if(!hasSystemFeature) {
                wifiAwareIncompatible = true
            }else {
                wifiAwareScanFailed = true
            }
        }
    }

}