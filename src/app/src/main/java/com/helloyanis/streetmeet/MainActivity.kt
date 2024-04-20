package com.helloyanis.streetmeet

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.remember
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
                            onDismissRequest = { wifiAwareIncompatible = false },
                            onConfirmation = { /* Action à effectuer lors de la confirmation */ },
                            dialogTitle = "Wi-Fi Aware incompatible",
                            dialogText = "Votre appareil n'est pas compatible avec cette fonctionnalité.",
                            icon = Icons.Default.Info // ou tout autre icône appropriée
                        )
                    } else if (wifiAwareDisabledDialogVisible) {
                        AlertDialog(
                            onDismissRequest = { wifiAwareDisabledDialogVisible = false },
                            onConfirmation = { /* Action à effectuer lors de la confirmation */ },
                            dialogTitle = "Wi-Fi Direct désactivé",
                            dialogText = "Veuillez activer le Wi-Fi Direct pour utiliser cette fonctionnalité.",
                            icon = Icons.Default.Info // ou tout autre icône appropriée
                        )
                    } else if (wifiAwareScanFailed) {
                        AlertDialog(
                            onDismissRequest = { wifiAwareScanFailed = false },
                            onConfirmation = { /*TODO*/ },
                            dialogTitle = "Autorisations insuffisantes",
                            dialogText = "Veuillez activer la détection d'appareils à proximité dans les paramètres de l'application",
                            icon = Icons.Default.Info
                        )
                    }
                }
            }
        }

        val hasSystemFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
        if (hasSystemFeature) {

            //Log nearby devices
            val wifiAwareManager = getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager
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
                Text("OK")
            }
        }
    )
}