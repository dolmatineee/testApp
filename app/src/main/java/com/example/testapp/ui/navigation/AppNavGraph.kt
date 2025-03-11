package com.example.testapp.ui.navigation

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.testapp.utils.toBase64


@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    typesReportsScreenContent: @Composable () -> Unit,
    blenderScreenContent: @Composable () -> Unit,
    acidScreenContent: @Composable () -> Unit,
    gelScreenContent: @Composable () -> Unit,
    historyScreenContent: @Composable () -> Unit,
    settingsScreenContent: @Composable () -> Unit,
    detailsScreenContent: @Composable () -> Unit,
    loginScreenContent: @Composable () -> Unit,
    signatureScreenContent: @Composable () -> Unit,
) {
    val sharedPreferences = LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

    NavHost(
        navController = navHostController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        homeScreenNavGraph(
            typesReportsScreenContent = typesReportsScreenContent,
            blenderScreenContent = blenderScreenContent,
            acidScreenContent = acidScreenContent,
            gelScreenContent = gelScreenContent
        )

        composable(Screen.History.route) {
            historyScreenContent()
        }

        composable(Screen.Settings.route) {
            settingsScreenContent()
        }

        composable(Screen.Details.route) {
            detailsScreenContent()
        }

        composable(Screen.Login.route) {
            loginScreenContent()
        }

        composable(Screen.Signature.route) {
            signatureScreenContent()
        }
    }
}