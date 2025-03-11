package com.example.testapp.ui.navigation

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.testapp.utils.toImageBitmap

fun NavGraphBuilder.homeScreenNavGraph(
    typesReportsScreenContent: @Composable () -> Unit,
    blenderScreenContent: @Composable () -> Unit,
    acidScreenContent: @Composable () -> Unit,
    gelScreenContent: @Composable () -> Unit,
) {
    navigation(
        startDestination = Screen.TypesReports.route,
        route = Screen.Home.route
    ) {
        composable(Screen.TypesReports.route) {
            typesReportsScreenContent()
        }
        composable(Screen.Blender.route) {
            blenderScreenContent()
        }
        composable(Screen.Acid.route) {
            acidScreenContent()
        }
        composable(Screen.Gel.route) {
            gelScreenContent()
        }
    }
}