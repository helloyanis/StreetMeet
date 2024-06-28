package com.helloyanis.streetmeet.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.helloyanis.streetmeet.R
import com.helloyanis.streetmeet.model.Message
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(navController: NavController){
    val messages = mutableListOf(
        Message("Yann", LocalDateTime.now(),"Salut Mec!"),
        Message("Aerith", LocalDateTime.now(),"Bonjour, je vend des fleurs")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.messageListButton)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = rememberVectorPainter(
                            image = Icons.Default.ArrowBack),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            items(messages.size){
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Text(text = "${messages[it].emitter}, ${messages[it].dateTime.dayOfMonth}/${messages[it].dateTime.monthValue}/${messages[it].dateTime.year} "
                            + stringResource(id = R.string.messageAt) + " ${messages[it].dateTime.hour}:${messages[it].dateTime.minute}")
                    Text(
                        text = messages[it].content,
                        Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                    )
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
