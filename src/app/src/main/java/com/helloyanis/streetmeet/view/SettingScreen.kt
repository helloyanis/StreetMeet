package com.helloyanis.streetmeet.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.helloyanis.streetmeet.R
import com.helloyanis.streetmeet.SharedPreferencesTalker

@Composable
fun SettingScreen(navController: NavController, context: Context) {
    var backgroundCheck by remember {
        mutableStateOf(false)
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
        mutableStateOf("")
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp)
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { navController.navigate("MainScreen") }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(id = R.string.backToMain),
                    colorFilter = ColorFilter.tint(
                        Color.White
                    )
                )
            }
            Text(
                text = stringResource(id = R.string.settingTitle),
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 30.dp)
            )
        }

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
            Switch(checked = backgroundCheck, onCheckedChange = { backgroundCheck = it })
        }
        
        OutlinedTextField(
            value = activationTime,
            onValueChange = { value ->
                if (value.length <= 3) activationTime = value.filter { it.isDigit() }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            label = {
                Text(text = stringResource(id = R.string.activationTimeSetting))
            },
            suffix = {
                Text(text = "minutes")
            },
            supportingText = {
                Text(text = "0 means constantly activate")
            }
        )
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
                        if (showDialogWithReason == "message")
                            SharedPreferencesTalker(context).setMessageInSharedPreferences(message)
                        else {
                            SharedPreferencesTalker(context).setNameInSharedPreferences(name)
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

            title = { Text(text = if (showDialogWithReason == "name") "Edit your name" else "Edit your message") },
            text = {
                TextField(
                    value =
                    if (showDialogWithReason == "name") name
                    else message
                    ,
                    onValueChange = {
                        if (showDialogWithReason == "name")
                            if (it.length in 1..20) name = it
                        else
                            if (it.length in 1..100) message = it
                    })
            }

        )
    }

}

@Preview
@Composable
fun SettingPreview() {
    SettingScreen(rememberNavController(), LocalContext.current)
}