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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, context: Context) {
    var checked by remember {
        mutableStateOf(false)
    }
    val sharedPreferencesTalker = SharedPreferencesTalker(context)
    var customMessage by remember{
        mutableStateOf(sharedPreferencesTalker.getMessageFromSharedPreferences())
    }

    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )

        //service en arrière plan
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

        //message customisé
        Text("setNewMessage")
        TextField(value = customMessage, onValueChange = {
            customMessage = it
        })
        Button(onClick = { sharedPreferencesTalker.setMessageInSharedPreferences(customMessage) }) {
            Text(text = "Valid Change")
        }
    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StreetMeetTheme {
        //Greeting("Android")
    }
}