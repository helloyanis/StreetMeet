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
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.helloyanis.streetmeet.R

@Composable
fun SettingScreen(navController: NavController){
    var backgroundCheck by remember {
        mutableStateOf(false)
    }


    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(vertical = 50.dp))
    {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { navController.navigate("MainScreen") }) {
                Image(painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(id = R.string.backToMain), colorFilter = ColorFilter.tint(
                        Color.White))
            }
            Text(text = stringResource(id = R.string.settingTitle), fontSize = 20.sp, modifier = Modifier.padding(start = 30.dp))
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.1f))

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
            Text(text = stringResource(id = R.string.backgroundAppText), Modifier.padding(end = 10.dp), fontSize = 20.sp)
            Switch(checked = backgroundCheck, onCheckedChange = {backgroundCheck = it})
        }

        Button(onClick = { /*TODO*/ }, Modifier.padding(top = 10.dp)) {
            Text(text = stringResource(id = R.string.activationTimeSetting))
        }
        Spacer(modifier = Modifier.fillMaxHeight(0.4f))

        Button(onClick = { /*TODO*/ },Modifier.fillMaxWidth(0.75f)) {
            Text(text = stringResource(id = R.string.nameSetting))
        }
        Button(onClick = { /*TODO*/ }, Modifier.padding(top = 10.dp).fillMaxWidth(0.75f)) {
            Text(text = stringResource(id = R.string.messageSetting))
        }
    }

}

@Preview
@Composable
fun SettingPreview(){
    SettingScreen(rememberNavController())
}