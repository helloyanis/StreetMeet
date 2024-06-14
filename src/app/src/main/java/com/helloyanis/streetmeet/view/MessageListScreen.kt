package com.helloyanis.streetmeet.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.helloyanis.streetmeet.Message
import com.helloyanis.streetmeet.R
import java.time.LocalDateTime

@Composable
fun MessageListScreen(navController: NavController){
    val messages = mutableListOf(Message("Yann", LocalDateTime.now(),"Salut Mec!"),
        Message("Aerith", LocalDateTime.now(),"Bonjour, je vend des fleurs"))


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
            Text(text = stringResource(id = R.string.messageListButton), fontSize = 20.sp, modifier = Modifier.padding(start = 30.dp))
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.1f))

        LazyColumn(Modifier.fillMaxWidth()) {
            items(messages.size){
                Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Text(text = "de: ${messages[it].emitter}, le " +
                            "${messages[it].dateTime.dayOfMonth}/${messages[it].dateTime.monthValue}/${messages[it].dateTime.year}" +
                            " à ${messages[it].dateTime.hour}:${messages[it].dateTime.minute}")
                    Text(text = messages[it].content,
                        Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(vertical = 10.dp).fillMaxWidth())
                }
            }
        }
    }

}


@Preview
@Composable
fun MessagePreview(){
    MessageListScreen(rememberNavController())
}
