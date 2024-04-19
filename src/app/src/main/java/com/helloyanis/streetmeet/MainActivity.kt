package com.helloyanis.streetmeet

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.helloyanis.streetmeet.ui.theme.StreetMeetTheme


class MainActivity : ComponentActivity() {
    private var wifiAwareDisabledDialogVisible by mutableStateOf(false)
    private var wifiAwareScanFailed by mutableStateOf(false)
    private var wifiAwareIncompatible by mutableStateOf(false)
    private var wifiAwareSubscribeStarted by mutableStateOf(false)
    private var wifiAwarePublishStarted by mutableStateOf(false)
    private var sendingNotification by mutableStateOf(false)
    private var discoverySession: DiscoverySession? = null
    private var showMessagePopup by mutableStateOf(false)
    private var messageText by mutableStateOf("")

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            StreetMeetTheme {
                if(checkSelfPermission(android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    val appPermission = arrayOf(
                        android.Manifest.permission.POST_NOTIFICATIONS,
                        android.Manifest.permission.NEARBY_WIFI_DEVICES
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        this
                    )

                    val appPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions(),
                        onResult = { permissions ->
                            val permissionsGranted =
                                permissions.values.reduce { acc, isPermissionGranted ->
                                    acc && isPermissionGranted
                                }

                            if (permissionsGranted) {
                                wifiAwareScanFailed = false
                                val intent = Intent(
                                    this,
                                    MainActivity::class.java
                                )
                                this.startActivity(intent)
                                this.finishAffinity()
                            } else {
                                wifiAwareScanFailed = true

                            }
                        }
                    )

                    LaunchedEffect(Unit) {
                        appPermissionLauncher.launch(appPermission)
                    }
                }
                
                val notificationService = NotificationService(this)
                notificationService.createChannelNotification()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (wifiAwareIncompatible) {
                        AlertDialog(
                            onDismissRequest = { finishAndRemoveTask() },
                            onConfirmation = {
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://developer.android.com/develop/connectivity/wifi/wifi-aware")
                                )
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
                                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                })
                            },
                            dialogTitle = "Autorisations insuffisantes",
                            dialogText = "Veuillez activer la détection d'appareils à proximité dans les paramètres de l'application",
                            icon = Icons.Default.Info,
                            confirmationText = "Param. autorisations"
                        )
                    } else if (sendingNotification) {
                        notificationService.showBasicNotification(
                            "Vous avez croisé quelqu'un",
                            "Quelqu'un est a proximité, votre message personnalisé à été envoyé")
                    } else {
                        Column {
                            if (wifiAwareSubscribeStarted) {
                                Text("Recherche d'appareils à proximité...",
                                    modifier = Modifier.padding(innerPadding)

                                )
                            }
                            if (wifiAwarePublishStarted) {
                                Text("Envoi de votre présence aux appareils à proximité..."
                                    , modifier = Modifier.padding(innerPadding))
                            }
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
            }
        }

        val hasSystemFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
        if (hasSystemFeature && checkSelfPermission(android.Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                //Log nearby devices
                val wifiAwareManager =
                    getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager
                if (!wifiAwareManager.isAvailable) {
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
                                println("1 Service discovered from peer: $peerHandle")
                                Toast.makeText(
                                    applicationContext,
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
                                println("Service discovered from peer: $peerHandle")
                                Toast.makeText(
                                    applicationContext,
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, context: Context) {
    var checked by remember {
        mutableStateOf(false)
    }
    var customMessage by remember{
        mutableStateOf(getMessageFromSharedPreferences(context))
    }

    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Text(text = "Activate Background Service")
        Switch(
            checked = checked,
            onCheckedChange = {
                if (it) {
                    context.startService(Intent(context, BackgroundService::class.java))
                    checked = true
                } else {
                    context.stopService(Intent(context, BackgroundService::class.java))
                    checked = false
                }
            },
        )
        Text("setNewMessage")
        TextField(value = customMessage, onValueChange = {
            customMessage = it
        })
        Button(onClick = { setMessageInSharedPreferences(context,customMessage) }) {
            Text(text = "Valid Change")
        }
    }

}

fun getMessageFromSharedPreferences(context: Context): String{
    val sharedPreferences = context.getSharedPreferences("StreetMeet_message", MODE_PRIVATE)
    sharedPreferences.getString("savedMessage","Hello there") ?: {
        Toast.makeText(context, "no sharedPreferences" , Toast.LENGTH_SHORT).show()
    }

    return sharedPreferences.getString("savedMessage","Hello there") ?: "Hello there"
}

fun setMessageInSharedPreferences(context: Context, newMessage: String){
    val sharedPreferences = context.getSharedPreferences("StreetMeet_message", MODE_PRIVATE)
    with(sharedPreferences.edit()){
        putString("savedMessage",newMessage)
        apply()
    }
    Toast.makeText(context, "new Message saved" , Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StreetMeetTheme {
        //Greeting("Android")
    }
}