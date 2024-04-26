package com.helloyanis.streetmeet

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.aware.AttachCallback
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.PublishConfig
import android.net.wifi.aware.PublishDiscoverySession
import android.net.wifi.aware.SubscribeConfig
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.helloyanis.streetmeet.ui.theme.StreetMeetTheme


private lateinit var manager: WifiAwareManager
var receiver: BroadcastReceiver? = null




class MainActivity : ComponentActivity() {

    private var wifiAwareDisabledDialogVisible by mutableStateOf(false)
    private var wifiAwareScanFailed by mutableStateOf(false)
    private var wifiAwareIncompatible by mutableStateOf(false)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            StreetMeetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (wifiAwareIncompatible) {
                        AlertDialog(
                            onDismissRequest = { finishAndRemoveTask() },
                            onConfirmation = {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com/develop/connectivity/wifi/wifi-aware"))
                                startActivity(browserIntent)
                                finishAndRemoveTask()
                                             },
                            dialogTitle = "Wi-Fi Aware incompatible",
                            dialogText = "Votre appareil n'est pas compatible avec cette fonctionnalité.",
                            icon = Icons.Default.Clear, // ou tout autre icône appropriée
                            confirmationText = "Plus d'informations"
                        )
                    } else if (wifiAwareDisabledDialogVisible) {
                        AlertDialog(
                            onDismissRequest = {
                                val intent = Intent(
                                    this,
                                    MainActivity::class.java
                                )
                                this.startActivity(intent)
                                this.finishAffinity()
                                               },
                            onConfirmation = {
                                startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
                            },
                            dialogTitle = "Wi-Fi désactivé",
                            dialogText = "Veuillez activer le Wi-Fi utiliser cette fonctionnalité. (Pas besoin d'Internet, juste d'activer le Wi-Fi)",
                            icon = Icons.Default.Info, // ou tout autre icône appropriée
                            confirmationText = "Activer le Wi-Fi"
                        )
                    } else if (wifiAwareScanFailed) {
                        AlertDialog(
                            onDismissRequest = { wifiAwareScanFailed = false },
                            onConfirmation = {
                                /* TODO */
                            },
                            dialogTitle = "Autorisations insuffisantes",
                            dialogText = "Veuillez activer la détection d'appareils à proximité dans les paramètres de l'application",
                            icon = Icons.Default.Info,
                            confirmationText = "Autorisations"
                        )
                    }
                }
            }
        }

        val hasSystemFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
        if (hasSystemFeature) {

            //Log nearby devices
            val wifiAwareManager = getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager
            if(!wifiAwareManager.isAvailable){
                wifiAwareDisabledDialogVisible = true
            }
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
                        }

                        override fun onServiceDiscovered(
                            peerHandle: PeerHandle,
                            serviceSpecificInfo: ByteArray?,
                            matchFilter: List<ByteArray>?
                        ) {
                            super.onServiceDiscovered(peerHandle, serviceSpecificInfo, matchFilter)
                            println("Service discovered from peer: $peerHandle")
                        }
                    }, null)

                    val subscribeConfig = SubscribeConfig.Builder()
                        .setServiceName("com.helloyanis.streetmeet")
                        .build()

                    session.subscribe(subscribeConfig, object : DiscoverySessionCallback() {
                        override fun onSubscribeStarted(session: SubscribeDiscoverySession) {
                            super.onSubscribeStarted(session)
                            println("Subscribe started")
                        }

                        override fun onServiceDiscovered(
                            peerHandle: PeerHandle,
                            serviceSpecificInfo: ByteArray?,
                            matchFilter: List<ByteArray>?
                        ) {
                            super.onServiceDiscovered(peerHandle, serviceSpecificInfo, matchFilter)
                            println("Service discovered from peer: $peerHandle")
                        }
                    }, null)
                }
            }, null)
        } else {
            wifiAwareIncompatible = true
        }
    }
}





@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StreetMeetTheme {
        Greeting("Android")
    }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    confirmationText: String = "OK",
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(confirmationText)
            }
        }
    )
}