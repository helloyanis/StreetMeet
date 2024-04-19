package com.helloyanis.streetmeet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.helloyanis.streetmeet.ui.theme.StreetMeetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StreetMeetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        this
                    )
                }
            }
        }


        stopService(Intent(this,BackgroundService::class.java))
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, context: Context) {
    var checked by remember {
        mutableStateOf(false)
    }

    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Text(text = "Activate Background Service")
        Switch(checked = checked, onCheckedChange = {
            if(it){
                context.startService(Intent(context, BackgroundService::class.java))
                checked = true
            }
            else{
                context.stopService(Intent(context,BackgroundService::class.java))
                checked = false
            }
        },)
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StreetMeetTheme {
        //Greeting("Android")
    }
}