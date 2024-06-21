package com.helloyanis.streetmeet.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.helloyanis.streetmeet.R


@Composable
fun MainScreen(navController: NavController, wifiAwareSubscribeStarted: Boolean, wifiAwarePublishStarted: Boolean, nearbyDevicesAmount: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { navController.navigate("setting") }) {
                Image(painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = stringResource(id = R.string.settingTitle) , colorFilter = ColorFilter.tint(Color.White))
            }
        }



        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        if (wifiAwareSubscribeStarted && wifiAwarePublishStarted) {
            Text(
                text = stringResource(id = R.string.searchingText),
                fontSize = 40.sp
            )
        }

        Spacer(modifier = Modifier.weight(0.3f))
        Button(
            onClick = { }, modifier = Modifier.size(200.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = nearbyDevicesAmount.toString(), fontSize = 60.sp)
                Text(text = stringResource(id = R.string.nearbyDevice))
            }

        }
        Spacer(modifier = Modifier.weight(0.3f))

        Button(onClick = { navController.navigate("messageList") },
            modifier = Modifier.padding(bottom = 40.dp, top = 10.dp)) {
            Text(text = stringResource(id = R.string.messageListButton))
        }
    }
}
@Preview
@Composable
fun Preview(){
    MainScreen(rememberNavController(), wifiAwarePublishStarted = false, wifiAwareSubscribeStarted = false, nearbyDevicesAmount = 0)
}

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    confirmationText: String = "OK",
    icon: ImageVector,
) {
    androidx.compose.material3.AlertDialog(
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