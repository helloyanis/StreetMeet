package com.helloyanis.streetmeet

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
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

private val intentFilter = IntentFilter()
private lateinit var channel: WifiP2pManager.Channel
private lateinit var manager: WifiP2pManager
var receiver: BroadcastReceiver? = null
private val peers = mutableListOf<WifiP2pDevice>()



class MainActivity : ComponentActivity() {

    private var wifiDirectDisabledDialogVisible by mutableStateOf(false)
    private var wifiDirectScanFailed by mutableStateOf(false)


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            StreetMeetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (wifiDirectDisabledDialogVisible) {
                        AlertDialog(
                            onDismissRequest = { wifiDirectDisabledDialogVisible = false },
                            onConfirmation = { /* Action à effectuer lors de la confirmation */ },
                            dialogTitle = "Wi-Fi Direct désactivé",
                            dialogText = "Veuillez activer le Wi-Fi Direct pour utiliser cette fonctionnalité.",
                            icon = Icons.Default.Info // ou tout autre icône appropriée
                        )
                    } else  if (wifiDirectScanFailed){
                        AlertDialog(
                            onDismissRequest = { /*TODO*/ },
                            onConfirmation = { /*TODO*/ },
                            dialogTitle = "Autorisations insuffisantes",
                            dialogText = "Veuillez activer la détection d'appareils à proximité dans les paramètres de l'application",
                            icon = Icons.Default.Info
                        )
                    }
                }
            }
        }
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                println("Success")
            }

            override fun onFailure(reasonCode: Int) {
                println("Fail $reasonCode")
                wifiDirectScanFailed=true
            }
        })




    }
    public override fun onResume() {
        super.onResume()
        receiver = WiFiDirectBroadcastReceiver(manager, channel, this)
        registerReceiver(receiver, intentFilter)
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    /*
    WIFI DIRECT FUNCTIONS
     */
    fun showWifiDirectDisabledDialog() {
        wifiDirectDisabledDialogVisible = true
    }

    fun updatePeerList(peerList: Collection<WifiP2pDevice>) {
        for (device in peerList) {
            println("Device Name: ${device.deviceName}, Device Address: ${device.deviceAddress}")
            //TODO : Display in a list on the device
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