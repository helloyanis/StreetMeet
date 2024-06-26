package com.helloyanis.streetmeet.view

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.helloyanis.streetmeet.R
import com.helloyanis.streetmeet.services.StreetMeetForegroundService
import com.helloyanis.streetmeet.utils.ServiceUtils
import com.helloyanis.streetmeet.utils.SharedPreferencesTalker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    context: Context,
    backgroundUse: Boolean
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = rememberVectorPainter(
                                image = Icons.Default.ArrowBack),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val appContext = LocalContext.current
        var backgroundService by remember {
            mutableStateOf(StreetMeetForegroundService())
        }
        var backgroundCheck by remember {
            mutableStateOf(ServiceUtils.isServiceRunning(appContext, StreetMeetForegroundService::class.java))
        }
        var showDialogWithReason by remember {
            mutableStateOf("none")
        }
        var name by remember {
            mutableStateOf(SharedPreferencesTalker(context).getNameFromSharedPreferences())
        }
        var message by remember {
            mutableStateOf(SharedPreferencesTalker(context).getMessageFromSharedPreferences())
        }
        var activationTime by remember {
            mutableIntStateOf(SharedPreferencesTalker(context).getActivationTimeFromSharedPreferences())
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.backgroundAppText),
                    Modifier.padding(end = 10.dp),
                    fontSize = 20.sp
                )
                Switch(
                    checked = backgroundCheck,
                    onCheckedChange = {
                        backgroundCheck = it
                        if (backgroundCheck) {
                            val serviceIntent = Intent(context, StreetMeetForegroundService::class.java)
                            serviceIntent.putExtra(
                                StreetMeetForegroundService.EXTRA_DURATION_MS,
                                activationTime * 60000L
                            )
                            appContext.startForegroundService(serviceIntent)
                        } else {
                            val serviceIntent = Intent(context, StreetMeetForegroundService::class.java)
                            appContext.stopService(serviceIntent)
                        }
                    },
                    Modifier.padding(start = 10.dp),
                    thumbContent = if (backgroundCheck) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.activationTimeSetting),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.padding(2.dp))
                OutlinedTextField(
                    value = activationTime.toString(),
                    onValueChange = {
                        activationTime = it.toIntOrNull() ?: 0
                        SharedPreferencesTalker(context)
                            .setActivationTimeInSharedPreferences(activationTime)
                    },
                    suffix = {
                        Text(text = "minutes")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.width(150.dp)
                )
                Text(text = stringResource(id = R.string.activationTimeSupportingText))
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.4f))
            Button(
                onClick = { showDialogWithReason = "name" },
                Modifier.fillMaxWidth(0.75f)
            ) {
                Text(text = stringResource(id = R.string.nameSetting))
            }
            Button(
                onClick = { showDialogWithReason = "message" },
                Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(0.75f)
            ) {
                Text(text = stringResource(id = R.string.messageSetting))
            }
        }

        if (showDialogWithReason != "none") {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    showDialogWithReason = "none"
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            when (showDialogWithReason) {
                                "message" -> SharedPreferencesTalker(context)
                                    .setMessageInSharedPreferences(message)
                                "name" -> SharedPreferencesTalker(context)
                                    .setNameInSharedPreferences(name)
                            }
                            showDialogWithReason = "none"
                        }
                    ) {
                        Text("Confirm")
                    }

                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialogWithReason = "none"
                        }
                    ) {
                        Text("Dismiss")
                    }
                },

                title = {
                    Text(
                        text =
                            when (showDialogWithReason) {
                                "name" -> "Edit your name"
                                "message" -> "Edit your message"
                                else -> ""
                            }
                    )
                },
                text = {
                    TextField(
                        value = when (showDialogWithReason) {
                            "name" -> name
                            "message" -> message
                            else -> ""
                        },
                        onValueChange = { value ->
                            when (showDialogWithReason) {
                                "name" -> if (value.length in 1..20) name = value
                                "message" -> if (value.length in 1..100) message = value
                            }
                        }
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun SettingPreview() {
    SettingScreen(rememberNavController(), LocalContext.current, true)
}