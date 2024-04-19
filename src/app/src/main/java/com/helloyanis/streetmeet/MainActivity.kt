package com.helloyanis.streetmeet

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
    var customMessage by remember{
        mutableStateOf(getMessageFromSharedPreferences(context))
    }

    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
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
        Text("setNewMessage")
        TextField(value = customMessage, onValueChange = {
            customMessage = it
        })
        Button(onClick = { setMessageInSharedPreferences(context,customMessage) }) {
            Text(text = "Valid Change")
        }
    }

}

fun getMessageFromSharedPreferences(context: Context): String{
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.sharedPreferencesMessageFileName), MODE_PRIVATE)
    sharedPreferences.getString("savedMessage","Hello there") ?: {
        Toast.makeText(context, "no sharedPreferences" , Toast.LENGTH_SHORT).show()
    }

    return sharedPreferences.getString("savedMessage","Hello there") ?: "Hello there"
}

fun setMessageInSharedPreferences(context: Context, newMessage: String){
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.sharedPreferencesMessageFileName), MODE_PRIVATE)
    with(sharedPreferences.edit()){
        putString("savedMessage",newMessage)
        apply()
    }

    Toast.makeText(context, "new Message saved" , Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StreetMeetTheme {
        //Greeting("Android")
    }
}