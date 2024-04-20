package com.helloyanis.streetmeet

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.helloyanis.streetmeet.ui.theme.StreetMeetTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StreetMeetTheme {
                val postNotificationPermission =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        TODO("VERSION.SDK_INT < TIRAMISU")
                    }

                val notificationService = NotificationService(this, "Yanis")
                notificationService.createChannelNotification()
                LaunchedEffect(key1 = true) {
                    if (!postNotificationPermission.status.isGranted) {
                        postNotificationPermission.launchPermissionRequest()
                    }
                }
                Column(modifier = Modifier.padding(50.dp)) {
                    Button(onClick = { notificationService.showBasicNotification() }) {
                        Text(text = "Basic notification")
                    }
                }
            }
        }
    }
}