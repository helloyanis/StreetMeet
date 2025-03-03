package com.helloyanis.streetmeet

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.helloyanis.streetmeet.utils.DataStoreTalker
import com.helloyanis.streetmeet.view.MainScreen
import com.helloyanis.streetmeet.view.MessageListScreen
import com.helloyanis.streetmeet.view.SettingScreen

@Composable
fun RootNavHost(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "mainScreen"
    ) {
        composable("mainScreen")
        {
            MainScreen(
                navController,
                nearbyDevicesAmount
            )
        }

        composable("messageList")
        {
            MessageListScreen(navController)
        }

        composable("setting")
        {
            SettingScreen(
                navController,
                LocalContext.current,
                DataStoreTalker(LocalContext.current)
            )
        }
    }
}