package com.helloyanis.streetmeet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.startActivity
import com.helloyanis.streetmeet.services.NotificationService
import com.helloyanis.streetmeet.view.AlertDialog


@Composable
fun CheckState(
    notificationService: NotificationService,
    finishAndRemoveTask: () -> Unit,
    context: Context,
    notificationTitle: String,
    notificationContent: String,
) {
    if (wifiAwareIncompatible) {
        AlertDialog(
            onDismissRequest = { finishAndRemoveTask() },
            onConfirmation = {
                startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com/develop/connectivity/wifi/wifi-aware")), null)
            },
            dialogTitle = getString(context, R.string.wifiAwareIncompatibleDialogTitle),
            dialogText = getString(context, R.string.wifiAwareIncompatibleDialogText),
            icon = ImageVector.vectorResource(R.drawable.wifi_tethering_error),
            confirmationText = getString(context, R.string.wifiAwareIncompatibleDialogConfirmationText)
        )
    } else if (wifiAwareDisabledDialogVisible) {
        AlertDialog(
            onDismissRequest = {

            },
            onConfirmation = {
                val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                val activity = context as? Activity
                activity?.let {
                    startActivityForResult(it, panelIntent, 0, null)
                }
            },
            dialogTitle = getString(context, R.string.wifiAwareDisabledDialogTitle),
            dialogText = getString(context, R.string.wifiAwareDisabledDialogText),
            icon = ImageVector.vectorResource(R.drawable.wifi_tethering_off),
            confirmationText = getString(context, R.string.wifiAwareDisabledDialogConfirmationText)
        )
    } else if (wifiAwareMissingPermission) {
        AlertDialog(
            onDismissRequest = {  },
            onConfirmation = {
                val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", context.packageName, null)
                settingsIntent.data = uri
                startActivity(context, settingsIntent, null)
            },
            dialogTitle = getString(context, R.string.wifiAwareMissingPermissionDialogTitle),
            dialogText = getString(context, R.string.wifiAwareMissingPermissionDialogText),
            icon = Icons.Default.Info,
            confirmationText = getString(context, R.string.wifiAwareMissingPermissionDialogConfirmationText)
        )
    } else if(wifiAwareAttachFailed) {
        AlertDialog(
            onDismissRequest = {  },
            onConfirmation = {
                startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/helloyanis/StreetMeet/blob/main/src/app/src/main/java/com/helloyanis/streetmeet/MainActivity.kt")), null)
            },
            dialogTitle = getString(context, R.string.wifiAwareAttachFailedDialogTitle),
            dialogText = getString(context, R.string.wifiAwareAttachFailedDialogText),
            icon = ImageVector.vectorResource(R.drawable.wifi_tethering_error),
            confirmationText = getString(context, R.string.wifiAwareAttachFailedDialogConfirmationText)
        )
    } else if (sendingNotification) {
        notificationService.send(
            notificationTitle,
            notificationContent,
            2
        )
    }
}