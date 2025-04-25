package com.app.opencallerid.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.app.opencallerid.ui.screens.BlockedNumbersScreen
//import com.app.opencallerid.ui.screens.CallLogScreen
import com.app.opencallerid.ui.screens.HomeScreen
//import com.app.opencallerid.ui.screens.SearchScreen

@Composable
fun OpenCallerApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
//        composable("search") {
//            SearchScreen(navController = navController)
//        }
//        composable("call_log") {
//            CallLogScreen(navController = navController)
//        }
//        composable("blocked") {
//            BlockedNumbersScreen(navController = navController)
//        }
    }
}