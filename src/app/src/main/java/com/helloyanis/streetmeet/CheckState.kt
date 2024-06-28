package com.helloyanis.streetmeet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
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
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://developer.android.com/develop/connectivity/wifi/wifi-aware")
                )
                context.startActivity(browserIntent)
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
                //Check if Wi-Fi has been enabled
                wifiAwareDisabledDialogVisible = false
            },
            onConfirmation = {

            },
            dialogTitle = "Wi-Fi désactivé",
            dialogText = "Veuillez activer le Wi-Fi utiliser cette fonctionnalité. (Pas besoin d'Internet, juste d'activer le Wi-Fi)",
            icon = Icons.Default.Info,
            confirmationText = "Activer le Wi-Fi"
        )
    } else if (wifiAwareScanFailed) {
        AlertDialog(
            onDismissRequest = { wifiAwareScanFailed = false },
            onConfirmation = {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
            },
            dialogTitle = "Autorisations insuffisantes",
            dialogText = "Veuillez activer la détection d'appareils à proximité dans les paramètres de l'application",
            icon = Icons.Default.Info,
            confirmationText = "Param. autorisations"
        )
    } else if (sendingNotification) {
        notificationService.send(
            notificationTitle,
            notificationContent,
            2
        )
    }
}