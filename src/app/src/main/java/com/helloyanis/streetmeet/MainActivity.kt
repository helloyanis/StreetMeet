package com.helloyanis.streetmeet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.helloyanis.streetmeet.ui.theme.StreetMeetTheme


class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StreetMeetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        this
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, context: Context) {
    var checked by remember {
        mutableStateOf(false)
    }
    val sharedPreferencesTalker = SharedPreferencesTalker(context)
    var customMessage by remember {
        mutableStateOf(sharedPreferencesTalker.getMessageFromSharedPreferences())
        mutableStateOf("place")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello",
            modifier = modifier
        )
        Spacer(modifier = Modifier.weight(0.7f))
        //service en arrière plan
        Button(
            onClick = {
                if (checked) {
                    context.startService(Intent(context, BackgroundService::class.java))
                    checked = true

                } else {
                    context.stopService(Intent(context, BackgroundService::class.java))
                    checked = false
                }
            }, modifier = Modifier.size(200.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                if (checked) Color.Blue
                else Color(0xFF696A8A)
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_connect),
                contentDescription = "connect icon",
                modifier = Modifier.size(150.dp)
            )
        }


        Spacer(modifier = Modifier.weight(0.3f))
        //message customisé
        Text("Your personal message:")
        TextField(value = customMessage, onValueChange = {
            customMessage = it
            sharedPreferencesTalker.setMessageInSharedPreferences(customMessage)
        }, modifier = Modifier.padding(bottom = 40.dp, top = 10.dp))
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StreetMeetTheme {
        //Greeting(modifier = Modifier.padding(10.dp))
    }
}